//
// Created by Rory Abraham on 12/13/18.
//

#include <signal.h>
#include "Buffer.h"
#include "Producer.h"
#include "Consumer.h"

#define MAX_BUF_SIZE 10
#define NUM_THREADS 5
#define EXECUTION_TIME 60

int main(int argc, char * argv[])
{
    // initialize buffer
    DataItem * rawBuffer = malloc(sizeof(DataItem[MAX_BUF_SIZE]));
    cbuf_handle_t cbuf = cbuf_init(rawBuffer, MAX_BUF_SIZE);

    // initialize producers
    producer_t * producerA = producer_init('A', 1, cbuf);
    producer_t * producerB = producer_init('B', 2, cbuf);
    producer_t * producerC = producer_init('C', 3, cbuf);

    // initialize consumers
    consumer_t * consumer1 = consumer_init('1', false, cbuf);
    consumer_t * consumer2 = consumer_init('2', true, cbuf);

    // initialize thread ids
    pthread_t producerA_thread;
    pthread_t producerB_thread;
    pthread_t producerC_thread;
    pthread_t consumer1_thread;
    pthread_t consumer2_thread;

    // create threads
    pthread_create(&producerA_thread, NULL, produce, producerA);
    pthread_create(&producerB_thread, NULL, produce, producerB);
    pthread_create(&producerC_thread, NULL, produce, producerC);
    pthread_create(&consumer1_thread, NULL, consume, consumer1);
    pthread_create(&consumer2_thread, NULL, consume, consumer2);

    // package thread ids in an array
    pthread_t threadIds[NUM_THREADS] = {
            producerA_thread,
            producerB_thread,
            producerC_thread,
            consumer1_thread,
            consumer2_thread
    };

    // let threads execute for 60 seconds
    sleep(EXECUTION_TIME);

    // terminate all threads
    for(int i = 0; i < NUM_THREADS; i++)
        pthread_kill(threadIds[i], SIGKILL);

    // free buffer
    cbuf_free(cbuf);

    exit(0);
}