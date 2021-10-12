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

/*
*********************************************************************************************************
*                                               Anpassungen
*********************************************************************************************************
*/
#define UBYTE                        INT8U
#define TASK_HIGH_PRIO                   4       /* Höchste Priorität - 0, 1, 2, 3 sind reserviert    */
#define EMPTYLINE      "                                                                                "

/*
*********************************************************************************************************
*                                               CONSTANTS
*********************************************************************************************************
*/

#define  TASK_STK_SIZE                 512       /* Size of each task's stacks (# of WORDs)            */       /* Number of identical tasks                          */
#define  N_TASKS	2
/*
*********************************************************************************************************
*                                               VARIABLES
*********************************************************************************************************
*/

OS_STK           TaskStk[N_TASKS][TASK_STK_SIZE];     /* Tasks stacks                                  */
OS_STK           TaskStartStk[TASK_STK_SIZE];
UBYTE             TaskData[N_TASKS];                   /* Parameters to pass to each task               */
OS_EVENT 		*Sem;	
UINT32 y=7;
/*
*********************************************************************************************************
*                                           FUNCTION PROTOTYPES
*********************************************************************************************************
*/

void   LowTask(void *data);                              /* Function prototypes of tasks                  */
void   HighTask(void *data);                              /* Function prototypes of tasks                  */
void   TaskStart(void *data);                         /* Function prototypes of Startup task           */
void ErrorHandler(char *str, UBYTE retnum, UBYTE returnOS);

/*$PAGE*/
/*
*********************************************************************************************************
*                                                MAIN
*********************************************************************************************************
*/

int main (void)
{

    setbuf(stdout, NULL);                                  /* Bildschirmpuffer deaktivieren            */
    PC_DispClrScr(DISP_FGND_WHITE + DISP_BGND_BLACK);      /* Init-Screen (VG weiß/HG schwarz)         */
    OSInit();                                              /* Init-uC/OS-I                             */
	Sem = OSSemCreate(1);

                                                           /* Start-Task erzeugen                      */
	OSTaskCreate(TaskStart, (void *) 0, (void *) &TaskStartStk[TASK_STK_SIZE - 1], TASK_HIGH_PRIO + 1);
    OSStart();                                             /* Start-uC/OS-I                            */

	return 0;
}

/*$PAGE*/
/*
*********************************************************************************************************
*                                              STARTUP TASK
*********************************************************************************************************
*/
void TaskStart (void *data)
{
    UBYTE  i;
    char   s[100];
    WORD   key;


    data = data;                                           /* Prevent compiler warning                 */

    PC_DispStr(26,  0, "uC/OS-II, The Real-Time Kernel", DISP_FGND_WHITE + DISP_BGND_RED);
    PC_DispStr(33,  1, "Kateryna Sydorenko", DISP_FGND_WHITE + DISP_BGND_BLACK);
    PC_DispStr(36,  3, "EXAMPLE #1", DISP_FGND_WHITE + DISP_BGND_BLACK);

    OSStatInit();                                          /* Initialize uC/OS-II's statistics         */
    
for (;;){
		if (PC_GetKey(&key) == TRUE) {   
			break;
        }
	}
  
	/* Create N_TASKS identical tasks           */
                                  /* Each task will display its own letter    */
    OSTaskCreate(HighTask, (void *)0, (void *)&TaskStk[0][TASK_STK_SIZE - 1], TASK_HIGH_PRIO + 2);
    OSTaskCreate(LowTask, (void *)0, (void *)&TaskStk[1][TASK_STK_SIZE - 1], TASK_HIGH_PRIO + 3);
     
   
    PC_DispStr(28, 24, "<-PRESS 'ESC' TO QUIT->", DISP_FGND_WHITE + DISP_BGND_BLACK);
    for (;;) {
        

        if (PC_GetKey(&key) == TRUE) {   
		/* See if key has been pressed              */
            if (key == 0x1B) {                             /* Yes, see if it's the ESCAPE key          */
                PC_DispClrScr(DISP_FGND_WHITE + DISP_BGND_BLACK);
				exit(0);                                   /* Exit                                     */
            }
        }
        //OSCtxSwCtr = 0;
        OSTimeDlyHMSM(0, 0, 0, 200);                         /* Wait one second                          */
    }
}

/*$PAGE*/
/*
*********************************************************************************************************
*                                                  TASKS
*********************************************************************************************************
*/
void HighTask (void *data)
{
    
    UBYTE err;
	char   s[100];
	data = data;

    for (;;) {
		OSSemPend (Sem, OS_TICKS_PER_SEC, &err);
		if (err == OS_ERR_TIMEOUT){
			PC_DispStr(20, y++, "HighTask timeout", DISP_FGND_BLUE + DISP_BGND_CYAN);
		}else{
			PC_DispStr(20, y++, "HighTask success", DISP_FGND_BLUE + DISP_BGND_CYAN);
			OSSemPost (Sem);
			OSTimeDlyHMSM(0,0,0,10);
		}
    }
}
void LowTask (void *data)
{
    
    UBYTE err;
	char   s[100];
	data = data;

    for (;;) {

        PC_DispStr(20, y++, "LowTask pend", DISP_FGND_BLUE + DISP_BGND_CYAN);
		OSSemPend (Sem, 0, &err);
		OSTimeDlyHMSM(0, 0, 5, 0);
        PC_DispStr(20, y++, "LowTask post", DISP_FGND_BLUE + DISP_BGND_CYAN);
		OSSemPost (Sem);
    }
}
/*$PAGE*/
/*
*********************************************************************************************************
*                                                ErrorHandler
*********************************************************************************************************
*/
void ErrorHandler(char *str, UBYTE retnum, UBYTE returnOS)
{
	char s[100];

	sprintf(s, "%s %5d", str, retnum);
	PC_DispStr(0, 21, s, DISP_FGND_WHITE + DISP_BGND_RED);
	OSTimeDlyHMSM(0, 0, 4, 0);

	if(returnOS)                                                                          /* Exit OS ? */
	{
	    PC_DispClrScr(DISP_FGND_WHITE + DISP_BGND_BLACK);
        exit(1);
	}
	else
	{
	    PC_DispStr(0, 21, EMPTYLINE, DISP_FGND_WHITE + DISP_BGND_BLACK);
	}
}

