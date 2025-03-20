

                                                uCOS-II
                                          The Real-Time Kernel

                        (c) Copyright 1992-1998, Jean J. Labrosse, Plantation, FL
                                           All Rights Reserved

                                                 V2.00

                                               EXAMPLE #1



#include includes.h



                                               Anpassungen


#define UBYTE                        INT8U
#define TASK_HIGH_PRIO               4        Höchste Priorität - 0, 1, 2, 3 sind reserviert    
#define EMPTYLINE                                                                                      



                                               CONSTANTS



#define  TASK_STK_SIZE                 512        Size of each task's stacks (# of WORDs)            
#define  N_TASKS                        10        Number of identical tasks                          



                                               VARIABLES



OS_STK           TaskStk[N_TASKS][TASK_STK_SIZE];      Tasks stacks                                  
OS_STK           TaskStartStk[TASK_STK_SIZE];
UBYTE             TaskData[N_TASKS];                    Parameters to pass to each task               

UINT ism = 0;
UINT shmptr = &ism;


                                           FUNCTION PROTOTYPES



void   Task(void data);                               Function prototypes of tasks                  
void   TaskStart(void data);                          Function prototypes of Startup task           
void ErrorHandler(char str, UBYTE retnum, UBYTE returnOS);

$PAGE


                                                MAIN



int main (void)
{
	char   s[100];
    setbuf(stdout, NULL);                                   Bildschirmpuffer deaktivieren            
    PC_DispClrScr(DISP_FGND_WHITE + DISP_BGND_BLACK);       Init-Screen (VG weißHG schwarz)         
    OSInit();                                               Init-uCOS-I                             
                                                            Start-Task erzeugen                      
	OSTaskCreate(TaskStart, (void ) 0, (void ) &TaskStartStk[TASK_STK_SIZE - 1], TASK_HIGH_PRIO + 1);
    OSStart();    
	sprintf(s, ism %d, ism);                      Display #tasks running                    
    PC_DispStr( 10, 15, s, DISP_FGND_BLUE + DISP_BGND_CYAN);	 Start-uCOS-I                            

	return 0;
}

$PAGE


                                              STARTUP TASK


void TaskStart (void data)
{
    UBYTE  i;
    UBYTE  retnum;
    char   s[100];
    WORD   key;


    data = data;                                            Prevent compiler warning                 

    PC_DispStr(26,  0, uCOS-II, The Real-Time Kernel, DISP_FGND_WHITE + DISP_BGND_RED);
    PC_DispStr(33,  1, Kateryna Sydorenko, DISP_FGND_WHITE + DISP_BGND_BLACK);
    PC_DispStr(10,  3, Press any key to start exept POWER-OFF button D, DISP_FGND_WHITE + DISP_BGND_BLACK);

    OSStatInit();                                           Initialize uCOS-II's statistics         
    PC_DispStr(0, 22, EMPTYLINE, DISP_FGND_WHITE + DISP_BGND_BLACK);

	for (;;){
		if (PC_GetKey(&key) == TRUE) {   
			break;
        }
	}

    for (i = 0; i  N_TASKS; i++) {   
	 Create N_TASKS identical tasks           
        TaskData[i] = i;                              Each task will display its own letter    
        retnum = OSTaskCreate(Task, (void )&TaskData[i], (void )&TaskStk[i][TASK_STK_SIZE - 1], TASK_HIGH_PRIO + i + 2);
        if(retnum != OS_ERR_NONE)
        {
            ErrorHandler(Fehler Task Create TaskStart, retnum, 1);
        }
    }
    PC_DispStr(28, 24, -PRESS 'ESC' TO QUIT-, DISP_FGND_WHITE + DISP_BGND_BLACK);
    for (;;) {

        if (PC_GetKey(&key) == TRUE) {   
		 See if key has been pressed              
            if (key == 0x1B) {                              Yes, see if it's the ESCAPE key          
                PC_DispClrScr(DISP_FGND_WHITE + DISP_BGND_BLACK);
				exit(0);                                    Exit                                     
            }
        }
        OSCtxSwCtr = 0;
		sprintf(s, ism %6d, ism);                     
        PC_DispStr(0, 19, s, DISP_FGND_BLUE + DISP_BGND_CYAN);
        OSTimeDlyHMSM(0, 0, 0, 200);                          Wait one second                          
		
    }
}

$PAGE


                                                  TASKS


void Task (void data)
{
	char   s[100];
	UINT32 number;
	UINT32 i;

	UBYTE id = (UBYTE ) data;

	for ( i = 0; i <= 10000; i++){
		
		number = shmptr;
		number++;
		OSTimeDlyHMSM(0, 0, 0, 10);
		shmptr = number;
	    sprintf(s, Task%d %6d, id+1, i);                                          
        PC_DispStr(10, 5+id, s, DISP_FGND_BLUE + DISP_BGND_CYAN);
	}
	
    for (;;) {
	                                
        PC_DispStr(x, y+1, s, DISP_FGND_BLUE + DISP_BGND_CYAN);
		OSTimeDlyHMSM(0, 0, 0, 200); 
    }}




                                                ErrorHandler


void ErrorHandler(char str, UBYTE retnum, UBYTE returnOS)
{
	char s[100];

	sprintf(s, %s %5d, str, retnum);
	PC_DispStr(0, 21, s, DISP_FGND_WHITE + DISP_BGND_RED);
	OSTimeDlyHMSM(0, 0, 4, 0);

	if(returnOS)                                                                           Exit OS  
	{
	    PC_DispClrScr(DISP_FGND_WHITE + DISP_BGND_BLACK);
        exit(1);
	}
	else
	{
	    PC_DispStr(0, 21, EMPTYLINE, DISP_FGND_WHITE + DISP_BGND_BLACK);
	}
}
