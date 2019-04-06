#ifndef COMMAND_LINE_H
#define COMMAND_LINE_H

#include <stdbool.h>

#define MAX_ARGS 128

// the representation of a command line is the following type.
// each white space separated word is a separate string in the
// arguments array.
// The arguments array is NULL terminated.
// If the command ended in a '&' that character is stripped from 
// the command and is indicated in the background field.
struct CommandLine
{
    char * arguments[MAX_ARGS];
    int argCount;
    bool background;     // command line ended with &
};

// returns true if line was parsed into a command containing at least
// one argument.
bool parseLine(struct CommandLine * command, char * line);

// print the CommandLine structure to standard output.
void printCommand(struct CommandLine * command);

// free all allocated memory associated with the command.
// This function should be called once the command is no longer needed.
void freeCommand(struct CommandLine * command);
    
#endif
