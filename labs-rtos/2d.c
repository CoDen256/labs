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
struct component{
    UBYTE amount;
    UBYTE color;
};

struct component Components[N_WAAGEN][N_COMPONENTS];
UBYTE TROCKEN_ZEIT;
UBYTE WASSER_ZEIT;
UBYTE NASS_ZEIT;

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

// Global settings
INT16U STEP = 1;


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
void fillWaagen(UBYTE id, UBYTE step);
void fillWaagenFromContainer(UBYTE id, UBYTE *container, UBYTE size, UBYTE step);
void fillMischerFromContainer(UBYTE *container, UBYTE size, UBYTE step);

void mischProcess(UBYTE duration, char* name);
void wasserZugabeProcess(UBYTE duration);
void entleerungsProcess();


// UI utility functions
void displayStaticContent(void);
void displayContainer(UBYTE* container, OS_EVENT* guardingSemaphore, UBYTE size, UBYTE x, UBYTE y, UBYTE width, UBYTE height);
void displayConfig(UBYTE x, UBYTE y);
void displaySize(UBYTE *container, UBYTE size, UBYTE x, UBYTE y);
void displayStepSize(UBYTE x, UBYTE y);

void displayStatus(char *status, UBYTE id);
void clearStatus(UBYTE id);

// utility
UBYTE getTotalComponentSize(UBYTE id);
UBYTE getFilledSize(UBYTE* container, UBYTE size);

void shuffle(UBYTE* array, UBYTE size);
UBYTE getLastFilledCell(const UBYTE* container, UBYTE size);
UBYTE getFirstEmptyCell(const UBYTE* container, UBYTE size);

void loadConfig(void);

void debug(UBYTE* array, UBYTE size, UBYTE width, UBYTE x, UBYTE y);
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

    UBYTE waagen1_id = 0;
    UBYTE waagen2_id = 1;

    OSTaskCreate(WaagenTask, (void *) &waagen1_id, (void *) &TaskStk[0][TASK_STK_SIZE - 1],TASK_HIGH_PRIO + 2);
    OSTaskCreate(WaagenTask, (void *) &waagen2_id, (void *) &TaskStk[1][TASK_STK_SIZE - 1],TASK_HIGH_PRIO + 3);
    OSTaskCreate(MischerTask, (void *) 0, (void *) &TaskStk[3][TASK_STK_SIZE - 1], TASK_HIGH_PRIO + 5);

    OSTaskCreate(KeyboardTask, (void *) 0, (void *) &TaskStk[2][TASK_STK_SIZE - 1], TASK_HIGH_PRIO + 4);
    OSTaskCreate(UITask, (void *) 0, (void *) &TaskStk[4][TASK_STK_SIZE - 1], TASK_HIGH_PRIO + 6);

    OSTaskCreate(GewichtSettingsTask, (void *) 0, (void *) &TaskStk[5][TASK_STK_SIZE - 1], TASK_HIGH_PRIO + 7);
    for (;;) {

        OSTimeDlyHMSM(0, 0, 1, 0);
    }
}


void KeyboardTask(void *data) {
    WORD key;
    UBYTE decrease = 1;
    UBYTE increase = 2;

    for (;;) {
        if (PC_GetKey(&key) == TRUE) { // Escape
            if (key == 0x1B) {
                PC_DispClrScr(DISP_FGND_WHITE + DISP_BGND_BLACK);
                exit(0);
            }
            if (key == 0x65) { // "E"
                loadConfig();
                fillWasserVentil();

                OSSemPost(StartWaagenSignal);
                OSSemPost(StartWaagenSignal);
            }
            if (key == 0x73) { // "S"
                OSSemPost(FillMischerSignal);
                OSSemPost(FillMischerSignal);
            }
            if (key == 0x6B){ // "K"
                OSMboxPost(UpdateStepSizeMbox, (void *)&decrease);
            }
            if (key == 0x6C){ // "L"
                OSMboxPost(UpdateStepSizeMbox, (void *)&increase);
            }
        }

        OSTimeDlyHMSM(0, 0, 0, 200);
    }
}

void WaagenTask(void *data) {
    UBYTE id = *(UBYTE *) data;

    UBYTE err;

    for (;;) {
        OSSemPend(StartWaagenSignal, 0, &err);

        fillWaagen(id, STEP);

        OSSemPend(FillMischerSignal, 0, &err);
        clearStatus(id);
        fillMischerFromContainer(WaagenContainers[id], MAX_CAPACITY_WAAGEN, STEP);
        OSSemPost(StartMischerSignal);
    }
}


void MischerTask(void *data) {
    UBYTE err;

    for (;;) {
        OSSemPend(StartMischerSignal, 0, &err);
        OSSemPend(StartMischerSignal, 0, &err);

        mischProcess(TROCKEN_ZEIT, "Trockenmischzeit");
        wasserZugabeProcess(WASSER_ZEIT);
        mischProcess(NASS_ZEIT, "Nassmischzeit");
        mischProcess(TROCKEN_ZEIT, "Trockenmischzeit");

        entleerungsProcess();
        displayStatus("Waiting to start", 0);
    }
}

void GewichtSettingsTask(void *data) {
    UBYTE err;
    UBYTE  msg;

    for (;;){

        msg = *(UBYTE *)OSMboxPend(UpdateStepSizeMbox, 0, &err);
        if (msg == 1){ // smaller stepsize
            if (STEP > 1){
                STEP -= 1;
            }
        }else if (msg == 2){ // bigger stepsize
            if (STEP < MAX_CAPACITY_WAAGEN){
                STEP += 1;
            }
        }
        OSTimeDlyHMSM(0, 0, 0, 200);
    }
}

void UITask(void *data) {

    displayStaticContent();
    for (;;) {
        displayContainer(WaagenContainers[0], WaagenSemaphores[0], MAX_CAPACITY_WAAGEN, 4, 2, 7, 8);
        displayContainer(WaagenContainers[1], WaagenSemaphores[1], MAX_CAPACITY_WAAGEN,  26, 2, 7, 8);
        displayContainer(MischerContainer, MischerSemaphore, MAX_CAPACITY, 10, 15, 18, 7);
        displayContainer(WasserContainer, WasserSemaphore, MAX_CAPACITY_WASSER, 16, 2, 5, 8);
        displayConfig(35, 10);
        displayStepSize(35, 5);
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

void fillWaagen(UBYTE id, UBYTE step) {
    char s[100];

    UBYTE size = getTotalComponentSize(id);
    UBYTE temp[size];
    UBYTE i, j, counter;
    for (i = 0, counter=0; i < N_COMPONENTS; i++){
        for (j = 0; j < Components[id][i].amount; j++){
            temp[counter++] = Components[id][i].color;
        }
    }

//    debug(temp, size, 10, 35, id*10);
    fillWaagenFromContainer(id, temp, size, step);


    clearStatus(id);
    sprintf(s, "Waagen %d ready", id);
    displayStatus(s, id);
}

void fillWaagenFromContainer(UBYTE id, UBYTE *container, UBYTE size, UBYTE step){
    UBYTE i, j, k;
    UBYTE err;
    char s[100];
    INT32 sum = getTotalComponentSize(id);

    i = getLastFilledCell(container, size);
    for (; i < size; ){
        OSSemPend(WaagenSemaphores[id], 0, &err);

        for (k = 0; k < step && i < size; k++, i++){
            j = getFirstEmptyCell(WaagenContainers[id], MAX_CAPACITY_WAAGEN);
            WaagenContainers[id][j] = container[size - i - 1];
            container[size - i - 1] = 0;
        }

        OSSemPost(WaagenSemaphores[id]);
        OSTimeDlyHMSM(0, 0, 0, 300);

        sprintf(s, "Filling waagen %d", sum);
        displayStatus(s, id);
    }
}

void fillMischerFromContainer(UBYTE *container, UBYTE size, UBYTE step) {
    UBYTE i, j, k;
    UBYTE err;

    clearStatus(0);
    displayStatus("Filling mischer", 0);

    i = getLastFilledCell(container, size);
    for (; i < size; ){
        OSSemPend(MischerSemaphore, 0, &err);

        for (k = 0; k < step && i < size; k++, i++){
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
        sprintf(s, "%s %ds/%ds", name, now-startTime, duration);
        displayStatus(s, 0);
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
        sprintf(s, "Wasserzugabe %ds/%ds", now-startTime, duration);
        displayStatus(s, 0);
        if (now - startTime >= duration){
            break;
        }
        OSTimeDlyHMSM(0, 0, 0, 100);
    }
}

void entleerungsProcess(){
    UBYTE err;
    UBYTE i = getLastFilledCell(MischerContainer, MAX_CAPACITY);
    for (; i < MAX_CAPACITY; i++){
        displayStatus("Entleerung", 0);
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
        PC_DispStr(22, 7, "-X", DISP_FGND_RED + DISP_BGND_BLACK);
    }
}

void displayConfig(UBYTE x, UBYTE y){
    char s[100];
    UBYTE i;
    UBYTE j;
    for (i = 0; i < N_WAAGEN; i++){
        for (j = 0; j < N_COMPONENTS; j++){
            sprintf(s, "Component %d.%d: %d", i, j, Components[i][j].amount);
            PC_DispStr(x+(i*20), y+(j*2), s, Components[i][j].color);
        }
    }

    sprintf(s, "Nassmischzeit: %d", NASS_ZEIT);
    PC_DispStr(x, y+6, s, DISP_FGND_WHITE + DISP_BGND_BLACK);
    sprintf(s, "Trockenmischzeit: %d", TROCKEN_ZEIT);
    PC_DispStr(x, y+7, s, DISP_FGND_WHITE + DISP_BGND_BLACK);
    sprintf(s, "Wasserzugabezeit %d", WASSER_ZEIT);
    PC_DispStr(x, y+8, s, DISP_FGND_WHITE + DISP_BGND_BLACK);

}

void displayContainer(UBYTE* container, OS_EVENT* guardingSemaphore, UBYTE size,  UBYTE x, UBYTE y, UBYTE width, UBYTE height){
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
    displaySize(container, size, x, y + height + 2);
    OSSemPost(guardingSemaphore);
}

void displayStepSize(UBYTE x, UBYTE y){
    char s[200];
    sprintf(s, "Step: %2d", STEP);
    PC_DispStr(x, y, s, DISP_FGND_WHITE + DISP_BGND_BLACK);
}

void displaySize(UBYTE *container, UBYTE size, UBYTE x, UBYTE y) {
    char s[100];
    sprintf(s, "%2d/%2d", getFilledSize(container, size), size);
    PC_DispStr(x, y, s, DISP_FGND_WHITE + DISP_BGND_BLACK);
}

void displayStatus(char *status, UBYTE id){
    clearStatus(id);
    char s[100];
    sprintf(s, "Status: %s", status);
    PC_DispStr(35, 20+id*2, s, DISP_FGND_WHITE + DISP_BGND_BLACK);
}

void clearStatus(UBYTE id){
    PC_DispStr(35, 20+id*2, "                                 ", DISP_FGND_WHITE + DISP_BGND_BLACK);
}

/*
*********************************************************************************************************
*                                                UTILITY FUNCTIONS
*********************************************************************************************************
*/
UBYTE getTotalComponentSize(UBYTE id){
    UBYTE sum = 0;
    UBYTE i = 0;
    for (i = 0; i < N_COMPONENTS; i++){
        sum += Components[id][i].amount;
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


void loadConfig(void){
    UBYTE colors[] = {DISP_BGND_GREEN + DISP_FGND_BLACK,
                      DISP_BGND_RED + DISP_FGND_BLACK,
                      DISP_BGND_WHITE + DISP_FGND_BLACK,
                      DISP_BGND_BLUE + DISP_FGND_BLACK,
                      DISP_BGND_MAGENTA + DISP_FGND_BLACK,
                      DISP_BGND_GREEN + DISP_FGND_BLACK,
    };
    UBYTE config[9];
    UBYTE amounts[6];
    UBYTE timeConfig[3];

    UBYTE line = 0;
    char buff[255];
    FILE *fp;
    fp = fopen("mischer.ini", "r");
    while(fgets(buff, sizeof(buff), fp)){
        config[line++] = atoi(buff);
    }
    fclose(fp);

    UBYTE i;
    UBYTE j;
    for (i = 0; i < 9; i++){
        if (i < 6){
            amounts[i] = config[i];
        }else{
            timeConfig[i-6] = config[i];
        }
    }
    NASS_ZEIT = timeConfig[0];
    TROCKEN_ZEIT = timeConfig[1];
    WASSER_ZEIT = timeConfig[2];

    for (i = 0; i < N_WAAGEN; i++){
        for (j = 0; j < N_COMPONENTS; j++){
            struct component waagenComponent;
            waagenComponent.amount = amounts[i * N_COMPONENTS + j];
            waagenComponent.color = colors[i * N_COMPONENTS + j];
            Components[i][j] = waagenComponent;
        }
    }
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
