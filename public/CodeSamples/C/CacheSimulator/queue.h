/*
 * File: queue.h
 * Author: Rory Abraham
 * Author: Ramon Levya
 * Author: Zack Oshana
 *
 * Created: 11/6/18
 */

#include <stdbool.h>

// A doubly-linked node with an unsigned int data
struct Node
{
    //an integer to represent the index in the cache
    unsigned int index;
    struct Node *next, *prev;
};

// A specially-implemented Queue ADT
struct Queue
{
    unsigned int nodeCount;     // number of initialized nodes in the queue
    unsigned int size;          // maximum number of nodes in the queue
    struct Node *front, *rear;
};

// useful boolean methods
bool contains(struct Queue* queue, struct Node* node);
bool isFull(struct Queue* queue);
bool isEmpty(struct Queue* queue);

// Where the action happens!
struct Node* dequeue(struct Queue* queue);
void enqueue(struct Queue* queue, struct Node* node);

// helper methods (unused because queue is declared globally in cache_associative_full.c
struct Node* newNode();
struct Queue* init(unsigned int size);

