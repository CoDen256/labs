/*
*********************************************************************************************************
*                                                uC/OS-II
*                                          The Real-Time Kernel
*
*                        (c) Copyright 1992-1998, Jean J. Labrosse, Plantation, FL
*                                           All Rights Reserved
*
*                                                 V2.00
*
*                                               EXAMPLE #1
*********************************************************************************************************
*/

#include "includes.h"
#include <stdio.h>
/*
*********************************************************************************************************
*                                               Anpassungen
*********************************************************************************************************
*/
#define UBYTE                        INT8U
#define TASK_HIGH_PRIO                   4       /* H?chste Priorit?t - 0, 1, 2, 3 sind reserviert    */
#define EMPTYLINE      "                                                                                "

/*
*********************************************************************************************************
*                                               CONSTANTS
*********************************************************************************************************
*/

#define  TASK_STK_SIZE                 512       /* Size of each task's stacks (# of WORDs)            */
#define  N_TASKS                        6       /* Number of identical tasks                          */
#define  MAX_CAPACITY_WAAGEN 		56
#define  MAX_CAPACITY_WASSER 		40
#define  MAX_CAPACITY 				126

#define  N_COMPONENTS 3
#define  N_WAAGEN   2
/*
*********************************************************************************************************
*                                               VARIABLES
*********************************************************************************************************
*/
// CONFIG
const UBYTE DECREASE_MSG = 1;
const UBYTE INCREASE_MSG = 2;
const UBYTE DEFAULT_STEP = 2;
const UBYTE COLORS[] = {DISP_BGND_GREEN + DISP_FGND_BLACK,
                        DISP_BGND_RED + DISP_FGND_BLACK,
                        DISP_BGND_WHITE + DISP_FGND_BLACK,
                        DISP_BGND_GRAY + DISP_FGND_BLACK,
                        DISP_BGND_MAGENTA + DISP_FGND_BLACK,
                        DISP_BGND_GREEN + DISP_FGND_BLACK,
};

typedef struct component{
    UBYTE amount;
    UBYTE color;
} COMPONENT;

typedef struct config {
    COMPONENT Components[N_WAAGEN][N_COMPONENTS];
    UBYTE trockenMischTime;
    UBYTE wasserZugabeTime;
    UBYTE nassMischTime;
    UBYTE step;
} CONFIG;

typedef struct waageData{
    UBYTE id;
    CONFIG* config;
} WAAGE_DATA;

typedef struct mischerData{
    CONFIG* config;
} MISCHER_DATA;

typedef struct uiData{
    CONFIG* config;
} UI_DATA;

typedef struct keyboardData{
    CONFIG* config;
} KEYBOARD_DATA;

// Task stacks
OS_STK TaskStk[N_TASKS][TASK_STK_SIZE];
OS_STK TaskStartStk[TASK_STK_SIZE];

// Semaphores as signals
OS_EVENT* StartWaagenSignal;
OS_EVENT* FillMischerSignal;
OS_EVENT* StartMischerSignal;

// Semaphores
OS_EVENT* ComponentsSemaphore;
OS_EVENT* MischerSemaphore;
OS_EVENT* WaagenSemaphores[N_WAAGEN];
OS_EVENT* WasserSemaphore;

// MsgBoxes
OS_EVENT* UpdateStepSizeMbox;

// Containers
UBYTE MischerContainer[MAX_CAPACITY];
UBYTE WaagenContainers[N_WAAGEN][MAX_CAPACITY_WAAGEN];
UBYTE WasserContainer[MAX_CAPACITY_WASSER];

/*
*********************************************************************************************************
*                                           FUNCTION PROTOTYPES
*********************************************************************************************************
*/
// Tasks
void TaskStart(void *data);
void WaagenTask(void *data);
void KeyboardTask(void *data);
void MischerTask(void *data);
void UITask(void *data);
void GewichtSettingsTask(void *data);

// Processes
void fillWasserVentil(void);
void fillWaage(UBYTE id, CONFIG* config);
void fillWaageFromContainer(UBYTE id, UBYTE *container, UBYTE size, CONFIG* config);
void fillMischerFromContainer(UBYTE *container, UBYTE size, CONFIG* config);

void mischProcess(UBYTE duration, char* name);
void wasserZugabeProcess(UBYTE duration);
void entleerungsProcess();


// UI utility functions
void displayStaticContent(void);
void
displayContainer(UBYTE *container, OS_EVENT *guardingSemaphore, UBYTE size, UBYTE x, UBYTE y, UBYTE width, UBYTE height,
                 UBYTE shift);
void displayConfig(UBYTE x, UBYTE y, CONFIG* config);
void displaySize(UBYTE *container, UBYTE size, UBYTE x, UBYTE y);
void displayStepSize(UBYTE x, UBYTE y, CONFIG* config);

void updateStatus(char *status, UBYTE line);

// utility
UBYTE getTotalComponentSize(COMPONENT* components);
UBYTE getFilledSize(UBYTE* container, UBYTE size);

void shuffle(UBYTE* array, UBYTE size);
UBYTE getLastFilledCell(const UBYTE* container, UBYTE size);
UBYTE getFirstEmptyCell(const UBYTE* container, UBYTE size);

void loadConfig(CONFIG* config);
void readFile(char *filename, UBYTE *lines);

void debug(UBYTE* array, UBYTE size, UBYTE width, UBYTE x, UBYTE y);
void debugSemaphore(OS_EVENT *pevent, UBYTE line, char* name);
void debugTask(UBYTE line, char* name, UBYTE priority);
void ErrorHandler(char *str, UBYTE retnum, UBYTE returnOS);


void displayValve(UBYTE isOpen);

/*
*********************************************************************************************************
*                                                MAIN
*********************************************************************************************************
*/

int main(void) {
    setbuf(stdout, NULL);
    PC_DispClrScr(DISP_FGND_WHITE + DISP_BGND_GRAY);
    OSInit();

    StartWaagenSignal = OSSemCreate(0);
    FillMischerSignal = OSSemCreate(0);
    StartMischerSignal = OSSemCreate(0);

    MischerSemaphore = OSSemCreate(1);
    WaagenSemaphores[0] = OSSemCreate(1);
    WaagenSemaphores[1] = OSSemCreate(1);
    WasserSemaphore = OSSemCreate(1);
    ComponentsSemaphore = OSSemCreate(1);

    UpdateStepSizeMbox = OSMboxCreate((void*)0);

    OSTaskCreate(TaskStart, (void *) 0, (void *) &TaskStartStk[TASK_STK_SIZE - 1], TASK_HIGH_PRIO + 1);
    OSStart();

    return 0;
}

/*$PAGE*/
/*
*********************************************************************************************************
*                                              STARTUP TASK
*********************************************************************************************************
*/

void TaskStart(void *data) {
    OSStatInit();

    static CONFIG DefaultConfig;
    DefaultConfig.step = DEFAULT_STEP;

    WAAGE_DATA w_data1;
    w_data1.id = 0;
    w_data1.config = &DefaultConfig;
    WAAGE_DATA w_data2;
    w_data2.id = 1;
    w_data2.config = &DefaultConfig;

    MISCHER_DATA m_data;
    m_data.config = &DefaultConfig;

    KEYBOARD_DATA k_data;
    k_data.config = &DefaultConfig;

    UI_DATA  ui_data;
    ui_data.config = &DefaultConfig;


    OSTaskCreate(WaagenTask, (void *) &w_data1, (void *) &TaskStk[0][TASK_STK_SIZE - 1], TASK_HIGH_PRIO + 2);
    OSTaskCreate(WaagenTask, (void *) &w_data2, (void *) &TaskStk[1][TASK_STK_SIZE - 1], TASK_HIGH_PRIO + 3);
    OSTaskCreate(MischerTask, (void *) &m_data, (void *) &TaskStk[3][TASK_STK_SIZE - 1], TASK_HIGH_PRIO + 5);

    OSTaskCreate(KeyboardTask, (void *) &k_data, (void *) &TaskStk[2][TASK_STK_SIZE - 1], TASK_HIGH_PRIO + 4);
    OSTaskCreate(UITask, (void *) &ui_data, (void *) &TaskStk[4][TASK_STK_SIZE - 1], TASK_HIGH_PRIO + 6);

    OSTaskCreate(GewichtSettingsTask, (void *) &DefaultConfig, (void *) &TaskStk[5][TASK_STK_SIZE - 1], TASK_HIGH_PRIO + 7);

    OSStart();
    for (;;) {

        OSTimeDlyHMSM(0, 0, 1, 0);
    }
}



void KeyboardTask(void *data) {
    KEYBOARD_DATA k_data = *(KEYBOARD_DATA *) data;
    WORD key;
    for (;;) {
        if (PC_GetKey(&key) == TRUE) { // Escape
            if (key == 0x1B) {
                PC_DispClrScr(DISP_FGND_WHITE + DISP_BGND_BLACK);
                exit(0);
            }
            if (key == 0x65) { // "E"
                loadConfig(k_data.config);
                fillWasserVentil();

                OSSemPost(StartWaagenSignal);
                OSSemPost(StartWaagenSignal);
            }
            if (key == 0x73) { // "S"
                OSSemPost(FillMischerSignal);
                OSSemPost(FillMischerSignal);
            }
            if (key == 0x6B){ // "K"
                OSMboxPost(UpdateStepSizeMbox, (void *)&DECREASE_MSG);
            }
            if (key == 0x6C){ // "L"
                OSMboxPost(UpdateStepSizeMbox, (void *)&INCREASE_MSG);
            }
        }

        OSTimeDlyHMSM(0, 0, 0, 200);
    }
}

void WaagenTask(void *data) {
    WAAGE_DATA w_data = *(WAAGE_DATA *) data;
    UBYTE id = w_data.id;
    CONFIG* config = w_data.config;
    char s[100];
    UBYTE err;

    for (;;) {
        sprintf(s, "Waagen %d is waiting", id+1);
        updateStatus(s, id);

        // FILL THE WAAGEN
        OSSemPend(StartWaagenSignal, 0, &err);

        fillWaage(id, config);
        
        sprintf(s, "Waagen %d is ready", id+1);
        updateStatus(s, id);

        // FILL MISCHER
        OSSemPend(FillMischerSignal, 0, &err);

        sprintf(s, "Waagen %d is being emptied", id+1);
        updateStatus(s, id);
        updateStatus("Filling mischer", 2);

        fillMischerFromContainer(WaagenContainers[id], MAX_CAPACITY_WAAGEN, config);
        OSSemPost(StartMischerSignal);
    }
}


void MischerTask(void *data) {
    MISCHER_DATA m_data = *(MISCHER_DATA *) data;
    CONFIG *config = m_data.config;
    UBYTE err;
    for (;;) {
        updateStatus("Mischer is waiting", 2);
        OSSemPend(StartMischerSignal, 0, &err);
        OSSemPend(StartMischerSignal, 0, &err);


        mischProcess(config->trockenMischTime, "Trockenmischzeit");
        wasserZugabeProcess(config->wasserZugabeTime);
        mischProcess(config->nassMischTime, "Nassmischzeit");
        mischProcess(config->trockenMischTime, "Trockenmischzeit");

        entleerungsProcess();
    }
}

void GewichtSettingsTask(void *data) {
    CONFIG* config = (CONFIG *) data;
    UBYTE err;
    UBYTE  msg;

    for (;;){

        msg = *(UBYTE *)OSMboxPend(UpdateStepSizeMbox, 0, &err);
        if (msg == DECREASE_MSG){ // smaller stepsize
            if (config->step > 1){
                config->step -= 1;
            }
        }else if (msg == INCREASE_MSG){ // bigger stepsize
            if (config->step < MAX_CAPACITY_WAAGEN){
                config->step += 1;
            }
        }
        OSTimeDlyHMSM(0, 0, 0, 200);
    }
}

void UITask(void *data) {
    UI_DATA ui_data = *(UI_DATA *) data;
    displayStaticContent();
    for (;;) {
//        debugSemaphore(WaagenSemaphores[0], -19, "Waagen 1 Sem");
//        debugSemaphore(WaagenSemaphores[1], -18, "Waagen 2 Sem");
//        debugSemaphore(MischerSemaphore, -17, "Mischer Sem");
//        debugSemaphore(WasserSemaphore, -16, "Wasser Sem");
//        debugSemaphore(StartWaagenSignal, -15, "StartWaagen Sem");
//        debugSemaphore(FillMischerSignal, -14, "FillMischer Sem");
//        debugSemaphore(StartMischerSignal, -13, "StartMischer Sem");

//        debugTask(-11, "WaagenTask", TASK_HIGH_PRIO + 2);
//        debugTask(-10, "WaagenTask", TASK_HIGH_PRIO + 3);
//        debugTask(-9, "KeyboardTask", TASK_HIGH_PRIO + 4);
//        debugTask(-8, "MischerTask", TASK_HIGH_PRIO + 5);
//        debugTask(-7, "UITask", TASK_HIGH_PRIO + 6);
//        debugTask(-6, "GewichtSettingsTask", TASK_HIGH_PRIO + 7);

        displayContainer(WaagenContainers[0], WaagenSemaphores[0], MAX_CAPACITY_WAAGEN, 4, 2, 7, 8, 1);
        displayContainer(WaagenContainers[1], WaagenSemaphores[1], MAX_CAPACITY_WAAGEN, 26, 2, 7, 8, 1);
        displayContainer(MischerContainer, MischerSemaphore, MAX_CAPACITY, 10, 15, 18, 7, 5);
        displayContainer(WasserContainer, WasserSemaphore, MAX_CAPACITY_WASSER, 16, 2, 5, 8, 0);

        displayConfig(35, 10, ui_data.config);
        displayStepSize(35, 5, ui_data.config);
        OSTimeDlyHMSM(0, 0, 0, 200);
    }
}

/*
*********************************************************************************************************
*                                           FILLING PROCESSES
*********************************************************************************************************
*/

void fillWasserVentil(void){
    UBYTE i;
    for (i = 0; i < MAX_CAPACITY_WASSER ; i++){
        WasserContainer[i] = DISP_BGND_CYAN + DISP_FGND_BLACK;
    }
}

void fillWaage(UBYTE id, CONFIG* config){
    COMPONENT* components = config->Components[id];
    UBYTE size = getTotalComponentSize(components);
    UBYTE temp[size];
    UBYTE i, j, counter;
    for (i = 0, counter=0; i < N_COMPONENTS; i++){
        for (j = 0; j < components[i].amount; j++){
            temp[counter++] = components[i].color;
        }
    }
    fillWaageFromContainer(id, temp, size, config);
}

void fillWaageFromContainer(UBYTE id, UBYTE *container, UBYTE size, CONFIG* config){
    UBYTE i, j, k;
    UBYTE err;
    char s[100];
    i = getLastFilledCell(container, size);
    for (; i < size; ){
        OSSemPend(WaagenSemaphores[id], 0, &err);

        for (k = 0; k < config->step && i < size; k++, i++){
            j = getFirstEmptyCell(WaagenContainers[id], MAX_CAPACITY_WAAGEN);
            WaagenContainers[id][j] = container[size - i - 1];
            container[size - i - 1] = 0;
        }

        OSSemPost(WaagenSemaphores[id]);
        OSTimeDlyHMSM(0, 0, 0, 300);

        sprintf(s, "Waagen %d is being filled: %2d/%2d", id+1, i, size);
        updateStatus(s, id);
    }
}

void fillMischerFromContainer(UBYTE *container, UBYTE size, CONFIG* config) {
    UBYTE i, j, k;
    UBYTE err;

    i = getLastFilledCell(container, size);
    for (; i < size; ){
        OSSemPend(MischerSemaphore, 0, &err);

        for (k = 0; k < config->step && i < size; k++, i++){
            j = getFirstEmptyCell(MischerContainer, MAX_CAPACITY);
            MischerContainer[j] = container[size - i - 1];
            container[size - i - 1] = 0;
        }

        OSSemPost(MischerSemaphore);
        OSTimeDlyHMSM(0, 0, 0, 300);
    }
}



void mischProcess(UBYTE duration, char* name) {
    UBYTE err;

    INT32 startTime = OSTimeGet() / OS_TICKS_PER_SEC;
    INT32 now;
    char s[100];
    for (;;) {
        OSSemPend(MischerSemaphore, 0, &err);
        shuffle(MischerContainer, MAX_CAPACITY);
        OSSemPost(MischerSemaphore);

        now = OSTimeGet() / OS_TICKS_PER_SEC;
        sprintf(s, "Mischer: %s %ds/%ds", name, now-startTime, duration);
        updateStatus(s, 2);
        if (now - startTime >= duration){
            break;
        }
        OSTimeDlyHMSM(0, 0, 0, 100);
    }
}

void wasserZugabeProcess(UBYTE duration){
    UBYTE j;
    UBYTE err;
    char s[100];

    INT32 startTime = OSTimeGet() / OS_TICKS_PER_SEC;
    INT32 now;

    for (;;){
        OSSemPend(MischerSemaphore, 0, &err);

        j = getFirstEmptyCell(MischerContainer, MAX_CAPACITY);
        MischerContainer[j] = WasserContainer[0];

        OSSemPost(MischerSemaphore);

        now = OSTimeGet() / OS_TICKS_PER_SEC;
        sprintf(s, "Mischer: Wasserzugabe %ds/%ds", now-startTime, duration);
        updateStatus(s, 2);
        if (now - startTime >= duration){
            break;
        }
        OSTimeDlyHMSM(0, 0, 0, 100);
        displayValve(1);
    }
    displayValve(0);
}

void entleerungsProcess(){
    UBYTE err;
    UBYTE i = getLastFilledCell(MischerContainer, MAX_CAPACITY);
    for (; i < MAX_CAPACITY; i++){
        updateStatus("Mischer is being emptied", 2);

        OSSemPend(MischerSemaphore, 0, &err);
        MischerContainer[MAX_CAPACITY-i-1] = 0;
        OSSemPost(MischerSemaphore);

        OSTimeDlyHMSM(0, 0, 0, 100);
    }
}

/*
*********************************************************************************************************
*                                     UI DISPLAY FUNCTIONS
*********************************************************************************************************
*/
void displayStaticContent(void) {

    UBYTE i;
    for(i = 0; i < 9; i++){
        PC_DispStr(3, 2+i, "|       |   |     |   |       |", DISP_FGND_WHITE + DISP_BGND_BLACK);
    }
    PC_DispStr(3, 11, "+_______+   +_____+   +_______+", DISP_FGND_WHITE + DISP_BGND_BLACK);

    for(i = 0; i < 7; i++){
        PC_DispStr(9, 16+i, "|                  |", DISP_FGND_WHITE + DISP_BGND_BLACK);
    }
    PC_DispStr(9, 23, "+__________________+", DISP_FGND_WHITE + DISP_BGND_BLACK);
    displayValve(0);
}

void displayValve(UBYTE isOpen) {
    if (isOpen == 0){
        PC_DispStr(22, 7, "-O", DISP_FGND_RED + DISP_BGND_BLACK);
    }else{
        PC_DispStr(22, 7, "-X", DISP_FGND_GREEN + DISP_BGND_BLACK);
    }
}

void displayConfig(UBYTE x, UBYTE y, CONFIG* config){
    char s[100];
    UBYTE i;
    UBYTE j;
    for (i = 0; i < N_WAAGEN; i++){
        for (j = 0; j < N_COMPONENTS; j++){
            sprintf(s, "Component %d.%d: %2d", i+1, j+1, config->Components[i][j].amount);
            PC_DispStr(x+(i*20), y+(j*2), s, config->Components[i][j].color);
        }
    }

    sprintf(s, "Nassmischzeit: %2d", config->nassMischTime);
    PC_DispStr(x, y+6, s, DISP_FGND_WHITE + DISP_BGND_BLACK);
    sprintf(s, "Trockenmischzeit: %2d", config->trockenMischTime);
    PC_DispStr(x, y+7, s, DISP_FGND_WHITE + DISP_BGND_BLACK);
    sprintf(s, "Wasserzugabezeit %2d", config->wasserZugabeTime);
    PC_DispStr(x, y+8, s, DISP_FGND_WHITE + DISP_BGND_BLACK);

}

void displayContainer(UBYTE *container, OS_EVENT *guardingSemaphore, UBYTE size, UBYTE x, UBYTE y, UBYTE width, UBYTE height,
                 UBYTE shift) {
    UBYTE i;
    UBYTE err;
    OSSemPend(guardingSemaphore, 0, &err);

    for (i = 0; i < size; i++){
        if (container[i] == 0){
            PC_DispChar(x + (i % width), y + height - (i / width), ' ', DISP_FGND_BLACK + DISP_BGND_BLACK);
        }else{
            PC_DispChar(x + (i % width), y + height - (i / width), ' ', container[i]);
        }
    }
    displaySize(container, size, x + shift, y + height + 2);
    OSSemPost(guardingSemaphore);
}

void displayStepSize(UBYTE x, UBYTE y, CONFIG* config){
    char s[200];
    sprintf(s, "Step: %2d", config->step);
    PC_DispStr(x, y, s, DISP_FGND_WHITE + DISP_BGND_BLACK);
}

void displaySize(UBYTE *container, UBYTE size, UBYTE x, UBYTE y) {
    char s[100];
    UBYTE filled = getFilledSize(container, size);
    if (filled / 100 >= 1) {
        sprintf(s, "%3d/%3d   ", filled, size);
    }else{
        sprintf(s, "%2d/%2d   ", filled, size);
    }
    PC_DispStr(x, y, s, DISP_FGND_WHITE + DISP_BGND_BLACK);
}

void updateStatus(char *status, UBYTE line) {
    char s[100];
    sprintf(s, "[%s]", status);
    PC_DispStr(35, 20+line, "                                     ", DISP_FGND_WHITE + DISP_BGND_BLACK);
    PC_DispStr(35, 20+line, s, DISP_FGND_WHITE + DISP_BGND_BLACK);
}

/*
*********************************************************************************************************
*                                                UTILITY FUNCTIONS
*********************************************************************************************************
*/
UBYTE getTotalComponentSize(COMPONENT* components){
    UBYTE sum = 0;
    UBYTE i = 0;
    for (i = 0; i < N_COMPONENTS; i++){
        sum += components[i].amount;
    }
    return sum;
}

UBYTE getFilledSize(UBYTE* container, UBYTE size){
    UBYTE sum = 0;
    UBYTE i = 0;
    char s[100];

    for (i = 0; i < size; i++){
        if (container[i] != 0){
            sum += 1;
        }
    }
    return sum;
}


void shuffle(UBYTE* array, UBYTE size){
    UBYTE i;
    UBYTE temp;
    UBYTE next;

    UBYTE firstEmpty = getFirstEmptyCell(MischerContainer, MAX_CAPACITY);
    for (i = 0; i < firstEmpty; i++){
        next = rand() % firstEmpty;
        // swap values
        temp = array[next];
        array[next] = array[i];
        array[i] = temp;
    }
}

// searches from the top to bottom the last filled cell
UBYTE getLastFilledCell(const UBYTE* container, UBYTE size){
    UBYTE last = 0;
    while (container[size - last - 1] == 0){last++;}
    return last;
}

// searches from the bottom to top the first unfilled cell
UBYTE getFirstEmptyCell(const UBYTE* container, UBYTE size){
    UBYTE first = 0;
    while (container[first] != 0){first++;}
    return first;
}


void loadConfig(CONFIG* config){
    UBYTE lines[9];

    readFile("mischer.ini", lines);

    UBYTE i, j;
    for (i = 0; i < N_WAAGEN; i++){
        for (j = 0; j < N_COMPONENTS; j++){
            COMPONENT waagenComponent;
            waagenComponent.amount = lines[i * N_COMPONENTS + j];
            waagenComponent.color = COLORS[i * N_COMPONENTS + j];
            config->Components[i][j] = waagenComponent;
        }
    }

    config->nassMischTime = lines[6];
    config->trockenMischTime = lines[7];
    config->wasserZugabeTime = lines[8];
}

void readFile(char *filename, UBYTE *lines) {
    char buff[255];
    UBYTE line = 0;
    FILE *fp;
    fp = fopen(filename, "r");
    while(fgets(buff, sizeof(buff), fp)){
        lines[line++] = atoi(buff);
    }
    fclose(fp);
}

/*$PAGE*/
/*
*********************************************************************************************************
*                                                ErrorHandler
*********************************************************************************************************
*/

void debug(UBYTE* array, UBYTE size, UBYTE width, UBYTE x, UBYTE y){
    UBYTE i=0;
    UBYTE j=0;
    UBYTE h=0;

    char s[100];
    for(;i < size;h++) {
        for (j = 0; j < width && i < size; j++, i++){
            sprintf(s, "%3d", array[i]);
            PC_DispStr(x+j*4, y+h, s, DISP_FGND_WHITE + DISP_BGND_RED);
        }
    }
}

void debugSemaphore(OS_EVENT *pevent, UBYTE line, char* name){
    OS_SEM_DATA data;
    UBYTE x,y;
    char s[100];
    OSSemQuery(pevent, &data);

    if (data.OSEventGrp == 0x00){
        sprintf(s, "%3s (%d)", name, data.OSCnt);
    }else{
        y = OSUnMapTbl[data.OSEventGrp];
        x = OSUnMapTbl[data.OSEventTbl[y]];

        sprintf(s, "%3s (%d): [Highest:%d]", name, data.OSCnt,  (y << 3) + x);
    }

    updateStatus(s, line);
}


void debugTask(UBYTE line, char* name, UBYTE priority){
    OS_TCB data;
    char* status;
    char s[100];

    OSTaskQuery(priority, &data);
    switch(data.OSTCBStat){
        case OS_STAT_RDY: status =  "READY"; break;
        case OS_STAT_SEM: status =  "SEM_PEND"; break;
        case OS_STAT_MBOX: status =  "MBOX_PEND"; break;
        case OS_STAT_SUSPEND : status =  "SUSPEND"; break;

    }
    sprintf(s, "%s (%d): %s %d", name, data.OSTCBPrio, status, data.OSTCBStatPend);
    updateStatus(s, line);
}

void ErrorHandler(char *str, UBYTE retnum, UBYTE returnOS) {
    char s[100];

    sprintf(s, "%s %5d", str, retnum);
    PC_DispStr(0, 21, s, DISP_FGND_WHITE + DISP_BGND_RED);
    OSTimeDlyHMSM(0, 0, 4, 0);

    if (returnOS)                                                                          /* Exit OS ? */
    {
        PC_DispClrScr(DISP_FGND_WHITE + DISP_BGND_BLACK);
        exit(1);
    } else {
        PC_DispStr(0, 21, EMPTYLINE, DISP_FGND_WHITE + DISP_BGND_BLACK);
    }
}
