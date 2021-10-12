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

#define  TASK_STK_SIZE                 512       /* Size of each task's stacks (# of WORDs)            */
#define  N_TASKS                        10       /* Number of identical tasks                          */

/*
*********************************************************************************************************
*                                               VARIABLES
*********************************************************************************************************
*/

OS_STK           TaskStk[N_TASKS][TASK_STK_SIZE];     /* Tasks stacks                                  */
OS_STK           TaskStartStk[TASK_STK_SIZE];
UBYTE             TaskData[N_TASKS];                   /* Parameters to pass to each task               */
OS_EVENT        *RandomSem;
//UINT32           RandomShifter = 0;

/*
*********************************************************************************************************
*                                           FUNCTION PROTOTYPES
*********************************************************************************************************
*/

void   Task(void *data);                              /* Function prototypes of tasks                  */
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
    UBYTE  retnum;
    char   s[100];
    WORD   key;


    data = data;                                           /* Prevent compiler warning                 */

    PC_DispStr(26,  0, "uC/OS-II, The Real-Time Kernel", DISP_FGND_WHITE + DISP_BGND_RED);
    PC_DispStr(33,  1, "Kateryna Sydorenko", DISP_FGND_WHITE + DISP_BGND_BLACK);
    PC_DispStr(36,  3, "EXAMPLE #1", DISP_FGND_WHITE + DISP_BGND_BLACK);

    PC_DispStr(0, 22, "Determining  CPU's capacity ...", DISP_FGND_WHITE + DISP_BGND_BLACK);
    OSStatInit();                                          /* Initialize uC/OS-II's statistics         */
    PC_DispStr(0, 22, EMPTYLINE, DISP_FGND_WHITE + DISP_BGND_BLACK);

    for (i = 0; i < N_TASKS; i++) {   
	/* Create N_TASKS identical tasks           */
        TaskData[i] = i;                             /* Each task will display its own letter    */
        retnum = OSTaskCreate(Task, (void *)&TaskData[i], (void *)&TaskStk[i][TASK_STK_SIZE - 1], TASK_HIGH_PRIO + i + 2);
        if(retnum != OS_ERR_NONE)
        {
            ErrorHandler("Fehler Task Create <TaskStart>:", retnum, 1);
        }
    }
    PC_DispStr( 0, 22, "#Tasks          : xxxxx  CPU Usage: xxx %", DISP_FGND_WHITE + DISP_BGND_BLACK);
    PC_DispStr( 0, 23, "#Task switch/sec: xxxxx", DISP_FGND_WHITE + DISP_BGND_BLACK);
    PC_DispStr(28, 24, "<-PRESS 'ESC' TO QUIT->", DISP_FGND_WHITE + DISP_BGND_BLACK);
    for (;;) {
        sprintf(s, "%5d", OSTaskCtr);                     /* Display #tasks running                    */
        PC_DispStr(18, 22, s, DISP_FGND_BLUE + DISP_BGND_CYAN);
        sprintf(s, "%3d", OSCPUUsage);                    /* Display CPU usage in %                    */
        PC_DispStr(36, 22, s, DISP_FGND_BLUE + DISP_BGND_CYAN);
        sprintf(s, "%5d", OSCtxSwCtr);                    /* Display #context switches per second      */
        PC_DispStr(18, 23, s, DISP_FGND_BLUE + DISP_BGND_CYAN);
        OSCtxSwCtr = 0;

        sprintf(s, "V%3.2f", (float)OSVersion() * 0.01);   /* Display version number as Vx.yy          */
        PC_DispStr(75, 24, s, DISP_FGND_YELLOW + DISP_BGND_BLUE);
        PC_GetDateTime(s);                                 /* Get and display date and time            */
        PC_DispStr(0, 24, s, DISP_FGND_BLUE + DISP_BGND_CYAN);

        if (PC_GetKey(&key) == TRUE) {   
		/* See if key has been pressed              */
            if (key == 0x1B) {                             /* Yes, see if it's the ESCAPE key          */
                PC_DispClrScr(DISP_FGND_WHITE + DISP_BGND_BLACK);
				exit(0);                                   /* Exit                                     */
            }
        }

        //OSCtxSwCtr = 0;
        OSTimeDlyHMSM(0, 0, 1, 0);                         /* Wait one second                          */
    }
}

/*$PAGE*/
/*
*********************************************************************************************************
*                                                  TASKS
*********************************************************************************************************
*/
void Task (void *data)
{
    UBYTE x;
    UBYTE y;
    UBYTE err;
	char   s[100];
	UBYTE i;

	UBYTE id = *(UBYTE *) data;

	UBYTE direction = 0; // 1 - descend, 0 - acsend
    srand(GetCurrentThreadId());
		
    x = 5 + id;
		y = 18;
    for (;;) {
		
	
		for (i=0; i < 13 ; i++)
		
		{
			
			if (y <= 5){
				direction = 1;
			}
			
			if (y >= 18){
				direction = 0;
			}
			
			if(direction == 0 ){
			PC_DispChar(x, y, '@',  DISP_FGND_CYAN + DISP_BGND_BLACK);
			y = y - 1;
			}
			else{
			PC_DispChar(x, y, ' ',  DISP_FGND_CYAN + DISP_BGND_BLACK);
			y = y + 1;
			}
			
		sprintf(s, "%d", direction);
        PC_DispStr(x, 19, s, DISP_FGND_WHITE + DISP_BGND_BLACK);
			
			
			OSTimeDlyHMSM(0, 0, 0, 100+id*10);
		}
		                           
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
