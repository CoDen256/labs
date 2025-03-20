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
#define  MAX_CAPACITY_WAAGEN 		51
#define  MAX_CAPACITY_WASSER 		24
#define  MAX_CAPACITY 				MAX_CAPACITY_WAAGEN*2+MAX_CAPACITY_WASSER

#define  N_COMPONENTS 3
#define  N_WAAGEN   2
/*
*********************************************************************************************************
*                                               VARIABLES
*********************************************************************************************************
*/
struct component{
    UBYTE amount;
    UBYTE color;
};

OS_STK TaskStk[N_TASKS][TASK_STK_SIZE];     /* Tasks stacks                                  */
OS_STK TaskStartStk[TASK_STK_SIZE];


OS_EVENT* FillMischerSignal;


OS_EVENT* ComponentsSemaphore;
OS_EVENT* MischerSemaphore;
OS_EVENT* WaagenSemaphores[N_WAAGEN];
OS_EVENT* WasserSemaphore;

struct component Components[N_WAAGEN][N_COMPONENTS];
UBYTE MischerContainer[MAX_CAPACITY];
UBYTE WaagenContainers[N_WAAGEN][MAX_CAPACITY_WAAGEN];
UBYTE WasserContainer[MAX_CAPACITY_WASSER];

UBYTE NASS_ZEIT;
UBYTE TROCKEN_ZEIT;
UBYTE WASSER_ZEIT;

INT16U step = 200;


/*
*********************************************************************************************************
*                                           FUNCTION PROTOTYPES
*********************************************************************************************************
*/
void loadConfig();

void TaskStart(void *data);

void WaagenTask(void *data);

void KeyboardTask(void *data);

void MischerTask(void *data);

void UITask(void *data);

void GewichtSettings(void *data);

void fillMischerWithContainer(UBYTE* container, UBYTE size);

void mischProcess(UBYTE duration, char* name);
void wasserZugabeProcess(UBYTE duration);
void entleerungsProcess();

void shuffle(UBYTE* array);

void displayStaticContent();
void displayContainer(UBYTE* container, OS_EVENT* guardingSemaphore, UBYTE size, UBYTE x, UBYTE y, UBYTE width, UBYTE height);

void displayConfig(UBYTE x, UBYTE y);

void displaySizes(UBYTE x, UBYTE y);

void displayStatus(char *status, UBYTE id);
void clearStatus(UBYTE id);

void ErrorHandler(char *str, UBYTE retnum, UBYTE returnOS);


INT32 componentsTotal(UBYTE id);
/*$PAGE*/
/*
*********************************************************************************************************
*                                                MAIN
*********************************************************************************************************
*/

int main(void) {
    setbuf(stdout, NULL);
    PC_DispClrScr(DISP_FGND_WHITE + DISP_BGND_GRAY);
    OSInit();

    FillMischerSignal = OSSemCreate(0);

    MischerSemaphore = OSSemCreate(1);
    WaagenSemaphores[0] = OSSemCreate(1);
    WaagenSemaphores[1] = OSSemCreate(1);
    WasserSemaphore = OSSemCreate(1);
    ComponentsSemaphore = OSSemCreate(1);

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

    OSTaskCreate(KeyboardTask, (void *) 0, (void *) &TaskStk[2][TASK_STK_SIZE - 1], TASK_HIGH_PRIO + 4);
    OSTaskCreate(UITask, (void *) 0, (void *) &TaskStk[4][TASK_STK_SIZE - 1], TASK_HIGH_PRIO + 6);
//    OSTaskCreate(GewichtSettings, (void *) 0, (void *) &TaskStk[5][TASK_STK_SIZE - 1], TASK_HIGH_PRIO + 7);
    for (;;) {

        OSTimeDlyHMSM(0, 0, 1, 0);
    }
}


void loadConfig(){
    UBYTE colors[] = {DISP_BGND_GREEN + DISP_FGND_BLACK,
                      DISP_BGND_RED + DISP_FGND_BLACK,
                      DISP_BGND_WHITE + DISP_FGND_BLACK,
                      DISP_BGND_GREEN + DISP_FGND_BLACK,
                      DISP_BGND_RED + DISP_FGND_BLACK,
                      DISP_BGND_WHITE + DISP_FGND_BLACK,
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


void WaagenTask(void *data) {
    UBYTE i, j;
    UBYTE counter = 0;
    UBYTE id = *(UBYTE *) data;
    UBYTE err;

    char s[100];

    INT32 sum = componentsTotal(id);

    for (i = 0; i < N_COMPONENTS; i++){
        for (j = 0; j < Components[id][i].amount/10; j++){
            OSSemPend(WaagenSemaphores[id], 0, &err);
            WaagenContainers[id][counter++] = Components[id][i].color;
            OSSemPost(WaagenSemaphores[id]);
            OSTimeDlyHMSM(0, 0, 0, step);
            sprintf(s, "Filling waagen %d: %d/%d", id, counter*10, sum);
            displayStatus(s, id);
        }
    }
    clearStatus(id);
    sprintf(s, "Waagen %d ready", id);
    displayStatus(s, id);

    OSSemPend(FillMischerSignal, 0, &err);
    clearStatus(id);
    fillMischerWithContainer(WaagenContainers[id], MAX_CAPACITY_WAAGEN);
    OSSemPost(FillMischerSignal);

    for (;;) {
        OSTimeDlyHMSM(0, 0, 1, 0);
    }
}

INT32 componentsTotal(UBYTE id){
    INT32 sum, i;
    for (i = 0; i < N_COMPONENTS; i++){
        sum += Components[id][i].amount;
    }
    return sum;
}

void fillMischerWithContainer(UBYTE* container, UBYTE size){
    UBYTE i, j;
    UBYTE err;
    while (container[size - i - 1] == 0){i++;}

    clearStatus(0);
    displayStatus("Filling mischer", 0);

    for (; i < size; i++){
        OSSemPend(MischerSemaphore, 0, &err);

        while (MischerContainer[j] != 0){j++;}
        MischerContainer[j] = container[size - i - 1];
        container[size - i - 1] = 0;

        OSSemPost(MischerSemaphore);
        OSTimeDlyHMSM(0, 0, 0, step);
    }
}

void KeyboardTask(void *data) {
    WORD key;
    UBYTE i = 0;
    UBYTE id1 = 0;
    UBYTE id2 = 1;
    for (;;) {
        if (PC_GetKey(&key) == TRUE) {
            if (key == 0x1B) {
                PC_DispClrScr(DISP_FGND_WHITE + DISP_BGND_BLACK);
                exit(0);
            }
            if (key == 0x65) {
                loadConfig();
                for (i = 0; i < MAX_CAPACITY_WASSER ; i++){
                    WasserContainer[i] = DISP_BGND_BLUE + DISP_FGND_BLUE;
                }
                OSTaskDel(TASK_HIGH_PRIO + 2);
                OSTaskDel(TASK_HIGH_PRIO + 3);
                OSTaskCreate(WaagenTask, (void *) &id1, (void *) &TaskStk[0][TASK_STK_SIZE - 1],TASK_HIGH_PRIO + 2);
                OSTaskCreate(WaagenTask, (void *) &id2, (void *) &TaskStk[1][TASK_STK_SIZE - 1],TASK_HIGH_PRIO + 3);
            }
            if (key == 0x73) {
                OSSemPost(FillMischerSignal);
                OSSemPost(FillMischerSignal);

                OSTaskDel(TASK_HIGH_PRIO + 5);
                OSTaskCreate(MischerTask, (void *) 0, (void *) &TaskStk[3][TASK_STK_SIZE - 1], TASK_HIGH_PRIO + 5);
            }
        }

        OSTimeDlyHMSM(0, 0, 0, 200);
    }
}

void MischerTask(void *data) {
    UBYTE err;

    OSSemPend(FillMischerSignal, 0, &err);
    OSSemPend(FillMischerSignal, 0, &err);

    mischProcess(TROCKEN_ZEIT, "Trockenmischzeit");
    wasserZugabeProcess(WASSER_ZEIT);
    mischProcess(NASS_ZEIT, "Nassmischzeit");
    mischProcess(TROCKEN_ZEIT, "Trockenmischzeit");

    entleerungsProcess();
    displayStatus("Waiting to start", 0);

    for (;;) {
        OSTimeDlyHMSM(0, 0, 1, 0);
    }
}


void mischProcess(UBYTE duration, char* name) {
    UBYTE err;
    UBYTE i;

    INT32 startTime = OSTimeGet() / OS_TICKS_PER_SEC;
    INT32 now;

    char s[100];
    for (;;) {
        OSSemPend(MischerSemaphore, 0, &err);
        shuffle(MischerContainer);
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

        while (MischerContainer[j] != 0){j++;}
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
    UBYTE i;
    for (i = 0; i < MAX_CAPACITY; i++){
        if (MischerContainer[MAX_CAPACITY-i-1] == 0) continue;
        displayStatus("Entleerung", 0);
        OSSemPend(MischerSemaphore, 0, &err);
        MischerContainer[MAX_CAPACITY-i-1] = 0;
        OSSemPost(MischerSemaphore);

        OSTimeDlyHMSM(0, 0, 0, 100);
    }
}

void shuffle(UBYTE* array){
    UBYTE i;
    UBYTE temp;
    UBYTE next;

    UBYTE last = 0;
    while (array[last] != 0) {last++;}
    for (i = 0; i < last; i++){
        next = rand() % last;
        // swap values
        temp = array[next];
        array[next] = array[i];
        array[i] = temp;
    }
}




void UITask(void *data) {

    displayStaticContent();
    for (;;) {
        displayContainer(WaagenContainers[0], WaagenSemaphores[0], MAX_CAPACITY_WAAGEN, 4, 2, 7, 8);
        displayContainer(WaagenContainers[1], WaagenSemaphores[1], MAX_CAPACITY_WAAGEN,  26, 2, 7, 8);
        displayContainer(MischerContainer, MischerSemaphore, MAX_CAPACITY, 10, 17, 18, 6);
        displayContainer(WasserContainer, WasserSemaphore, MAX_CAPACITY_WASSER, 17, 2, 3, 8);
        displayConfig(35, 10);
        OSTimeDlyHMSM(0, 0, 0, 200);
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

void displayStaticContent() {

    UBYTE i;
    for(i = 0; i < 9; i++){
        PC_DispStr(3, 2+i, "|       |    |   |    |       |", DISP_FGND_WHITE + DISP_BGND_BLACK);
    }
    PC_DispStr(3, 11, "+_______+    +___+    +_______+", DISP_FGND_WHITE + DISP_BGND_BLACK);

    for(i = 0; i < 7; i++){
        PC_DispStr(9, 17+i, "|                  |", DISP_FGND_WHITE + DISP_BGND_BLACK);
    }
    PC_DispStr(9, 24, "+__________________+", DISP_FGND_WHITE + DISP_BGND_BLACK);
    PC_DispStr(21, 7, "-O", DISP_FGND_RED + DISP_BGND_BLACK);
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
    OSSemPost(guardingSemaphore);
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

void GewichtSettings(void *data) {
//    WORD key;
//    char s[200];
//    for (;;){
//        if (PC_GetKey(&key) == TRUE) {
//            if (key == 0x6B) {
//                step += 5;
//            }
//            if (key == 0x6C) {
//                step -= 5;
//            }
//        }
//        OSTimeDlyHMSM(0, 0, 0, 200);
//    }
}

void displaySizes(UBYTE x, UBYTE y){

}



/*$PAGE*/
/*
*********************************************************************************************************
*                                                ErrorHandler
*********************************************************************************************************
*/
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
