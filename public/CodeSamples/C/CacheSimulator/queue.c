/*
 * File: queue.c
 * Author: Rory Abraham
 * Author: Ramon Levya
 * Author: Zack Oshana
 *
 * Created: 11/6/18
 */

#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include "queue.h"

bool contains(struct Queue* queue, struct Node* node)
{
    //if there is only one element in the queue
    if(queue->nodeCount == 1)
    {
        return queue->front->index == node->index;
    }

    // traverse the queue from the front until we find the param node
    struct Node* curr = queue->front;
    while(curr->next != NULL)
    {
        if(curr->index == node->index)
            return true;

        curr = curr->next;
    }
    return false;
}

bool isFull(struct Queue* queue)
{
    return queue->nodeCount == queue->size;
}

bool isEmpty(struct Queue* queue)
{
    return queue->nodeCount == 0;
}

/*
 * Returns a pointer to the rear element in the queue
 */
//TODO: make sure we're updating the front and rear nodes
struct Node* dequeue(struct Queue* queue)
{
    //if queue is empty
    if(isEmpty(queue))
        return NULL;

    //if queue has only one element
    if(queue->front == queue->rear)
        queue->front = NULL;

    //remove last element and change rear
    struct Node* last = queue->rear;
    queue->rear = queue->rear->prev;

    if(queue->rear)
        queue->rear->next = NULL;

    //decrement number of nodes
    queue->nodeCount--;

    return last;
}

//MODIFIED from standard to move nodes from middle of queue back to front rather than allowing duplicates
//TODO: make sure we're updating the front and rear nodes
void enqueue(struct Queue* queue, struct Node *newNode)
{
    //if this is the first time putting something in the queue
    if(queue->front == NULL)
    {
        queue->front = newNode;
        queue->rear = newNode;
        queue->nodeCount++;
        return;
    }

    //if queue already contains an equivalent node (the same cache block in our case)
    if(contains(queue, newNode))
    {
        //if there is only one element in the queue and it is the same as the one we are enqueuing
        if(queue->nodeCount == 1)
        {
            //do nothing
            return;
        }

        //find the equivalent node in the queue
        struct Node* curr = queue->front;
        while(curr->next != NULL)
        {
            if(curr->index == newNode->index)
                break;

            curr = curr->next;
        }

        //if curr is at the front of the queue
        if(curr->prev == NULL && curr->next != NULL)
        {
            //do nothing
            return;
        }
        //if curr is sandwiched between two nodes
        else if(curr->next != NULL && curr->prev != NULL)
        {
            //connect the two nodes on either side of curr
            curr->prev->next = curr->next;
            curr->next->prev = curr->prev;
        }
        //if curr is at the rear of the queue
        else if(curr->next == NULL && curr->prev != NULL)
        {
            //previous becomes the new rear
            queue->rear = curr->prev;
            curr->prev->next = NULL;
        }


        //move curr to the front of the queue
        curr->next = queue->front;
        queue->front->prev = curr;
        queue->front = curr;
        curr->prev = NULL;

        return;
    }

    //if queue is full deque from rear
    if(isFull(queue))
    {
        dequeue(queue);
    }

    //put new node in front of queue
    newNode->next = queue->front;

    //if queue is empty
    if(isEmpty(queue))
    {
        queue->front = newNode;
        queue->rear = newNode;
    }
    else
    {
        queue->front->prev = newNode;
        queue->front = newNode;
        queue->front->prev = NULL;
    }

    //increment node count
    queue->nodeCount++;
}

// Utility function to create a new node
struct Node* newNode()
{
    //allocate memory and set next and prev to NULL
    struct Node* newNode = (struct Node*) malloc(sizeof(struct Node));
    newNode->next = NULL;
    newNode->prev = NULL;
    return newNode;
}

// Queue constructor
struct Queue* init(unsigned int size)
{
    struct Queue* queue = (struct Queue*) malloc(sizeof(struct Queue));
    queue->nodeCount = 0;
    queue->front = NULL;
    queue->rear = NULL;
    queue->size = size;
    return queue;
}