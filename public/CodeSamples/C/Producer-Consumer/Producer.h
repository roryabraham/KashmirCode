//
// Created by Rory Abraham on 12/12/18.
//

#ifndef COMP280_HWK9_PRODUCER_H
#define COMP280_HWK9_PRODUCER_H

/// type declaration for a producer type
typedef struct producer_t producer_t;

/// initializer for a producer type
producer_t * producer_init(char producerID, unsigned sleepDuration, cbuf_handle_t cbuf);

/// will tell a producer to start producing dataItems at a rate of 1 item/sleepDuration seconds
void produce(producer_t * producer);

#endif //COMP280_HWK9_PRODUCER_H
