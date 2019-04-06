#include "command_line.h"
#include <ctype.h>
#include <string.h>
#include <stdlib.h>
#include <stdio.h>

bool parseLine(struct CommandLine * command, char * line)
{
    // initialize
    command->argCount = 0;
    command->background = 0;
    int i;
    for (i = 0; i<MAX_ARGS; ++i)
    {
        command->arguments[i] = NULL;
    }

    // if no line is provided just return now.
    if (line == NULL) return false;  // no line provided

    // look for an '&' at the end of the line
    int len = strlen(line);
    while (len >= 0)
    {
        if (isspace(line[len]) || (line[len] == '\0'))
        {
            --len;
        } else {
            if (line[len] == '&')
            {
                command->background = true;
                line[len] = ' ';  // git rid of & - it is processed
            }
            break;  // ending non-space found
        }
    }

    // was anything left on the line?
    if (len < 0) return false;

    // break the line into white space separated words
    const char * lp = line; 
    for (;;)
    {
        const char * start;
        // skip white space
        while (isspace(*lp))
        {
            ++lp;
        }
        start = lp;
        // go until the end of the string or the next white space
        while (*lp && ! isspace(*lp))
        {
            ++lp;
        }
        int len = lp - start;
        if (len == 0)
        {
            break; // must be at the end of the line
        }

        // add the word to the arguments
        char * ap = malloc(len+1);   // allow for '\0'
        memcpy(ap, start, len);
        ap[len] = '\0';
        command->arguments[command->argCount++] = ap;
        
        // is the command too long?
        // the last entry in arguments must contain a NULL so if
        // we just used that slot report this as an error
        if (command->argCount == MAX_ARGS)
        {
            freeCommand(command);
            fprintf(stderr, "command too long\n");
            return false;
        }

        if (*lp == '\0')
        {
            break;
        }
    }

    return command->argCount > 0;
}


void printCommand(struct CommandLine * command)
{
    int i;
    for (i=0; i<command->argCount; ++i)
    {
        printf("%d: %s\n", i, command->arguments[i]);
    }
    if (command->background)
    {
        printf("background\n");
    }
}


void freeCommand(struct CommandLine * command)
{
    int i;
    for (i=0; i<command->argCount; ++i)
    {
        free((void *)command->arguments[i]);
        command->arguments[i] = NULL;
    }
    command->argCount = 0;
}

