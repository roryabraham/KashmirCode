#include "command_line.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>
#include <unistd.h>
#include <errno.h>
#include <signal.h>

#define MAX_LINE_LENGTH 512

int builtin_command(char ** argv);
pid_t pid;  //process ID

void handle_SIGCHLD(int sig)
{
    int saved_errno = errno;
    int status;
    while (waitpid((pid_t)(-1), &status, WNOHANG) > 0)
    {
        if(WIFEXITED(status) == true)
            printf("Child process terminated with exit status %d\n", WEXITSTATUS(status));
        else if(WIFSIGNALED(status) == true)
            printf("Child process terminated by signal %d\n", WTERMSIG(status));
        else
            printf("Error occurred in reaping\n");
    }
    errno = saved_errno;
}

// sigint() function definition
void handle_SIGINT(int sig)
{
    //kill(pid, SIGINT);
    printf("caught SIGINT\n");
}

int main(int argc, const char **argv)
{
    // interrupt command should not interrupt shell. Exit shell via builtin-command "quit"
    signal(SIGINT, &handle_SIGINT);

    //Set signal handler for child processes
    signal(SIGCHLD,&handle_SIGCHLD);

    char cmdline[MAX_LINE_LENGTH];
    struct CommandLine command;
    //pid_t pid;  //process ID

    for (;;)
    {
        printf("> ");
        fgets(cmdline, MAX_LINE_LENGTH, stdin);
        if (feof(stdin)) 
        {
            exit(0);
        }

        bool gotLine = parseLine(&command, cmdline);
        if (gotLine) 
        {
            printCommand(&command);

            //if command is not builtin
            if(builtin_command(command.arguments) == 0)
            {
                setpgid(0,0);
                // create child process
                if((pid = fork()) == 0)     //if fork is successful
                {
                    signal(SIGINT,handle_SIGINT);

                    if (execvp(command.arguments[0], command.arguments) < 0)
                    {
                        printf("%s: Command not found.\n", command.arguments[0]);
                        exit(0);
                    }
                }
                //else printf("Parent regains execution!\n");

                // parent waits for foreground job to terminate
                if(!command.background)
                {
                    int status;
                    if(waitpid(pid, &status, 0) < 0)
                    {
                        //printf("waitpid error! exiting...\n");
                    }
                    else
                        printf("%d %s", pid, command);
                }
            }

            freeCommand(&command);
        }
    }
}

/*
 * Receives argv and checks first argument to see if it matches any of the built-in commands
 * If it does, it will execute the built-in command and return 1
 * Else, it will return 0
 */
int builtin_command(char ** argv)
{
    if(strcmp(argv[0],"quit") == 0)
        exit(0);
    if(strcmp(argv[0],"cd") == 0)
    {
        chdir(argv[1]);
        return 1;
    }
    return 0;
}
