#ifndef __SHE_PRIME_TASKER_H__
    #define __SHE_PRIME_TASKER_H__
    
//#include <iostream>
#include <pthread.h>
#include "linkedlist.h"
using namespace std;


// Search thread functions, for calling by pthread_create().
template<class X>
void* search_head_thread(void* x);

template<class X>
void* search_tail_thread(void* x);

template<class X>
void* search_prime_thread(void* x);


template<class X>
class PrimeTasker {
  public:
    PrimeTasker();

    // The task function.
    X runTask(X n);

  private:    
    static const size_t TASK_COUNT = 47;  // Count of threads for prime number search
    static const size_t INCREMENT = 210;
    static const size_t FIRST_BASE = 1050;
    static const size_t INIT_PRIME_COUNT = 172;
    // Base data for the fast prime number search algorithm, initialized in implementation area.
    static const X task_base[];
    static const X prime1050[];

    // Class level private members.
    LL<Prime<X>, X> list;
    X next_start; // should be dividable by INCREMENT.
    
    /**
     * Starts two threads to scan from the head and rear of the known primes list.
     * Once finds a prime to divide, all the thread exits and return the result.
     */
    void runTask1(X n);
    // Threads for running task1
    void* searchHead();
    void* searchTail();
    // Shared variables for running task1
    X hp, tp; // head prime and tail prime
    
    /**
     * Run TASK_COUNT threads to scan and find the primes from next_start+task_base[i], and increase by INCREMENT.
     * Once finds a prime can be devided by the given number, stop other threads.
     * Finally, find out the "next_start" position and remove the prime numbers (which are not sure to form a 
     * continuous prime numbers list)after the "next_start".
     */
    void runTask2(X n);
    // Thread for running task2
    void* searchPrime(size_t x);
    // Shared variables for running task2
    X max_p[TASK_COUNT]; // The max known prime got by each task;
    X top_p; // The biggest known prime
        
    // Shared variables for both task1 and task2
    bool task_not_done;
    X big_num;  // The number to be defactored.
    X result;  // The result.
    pthread_mutex_t mutex, sync_mutex;
    // Shared private functions
    bool isPrime(X x); // check whether a number is prime

    // for Pthread calling
    friend void* search_head_thread<X>(void* x);
    friend void* search_tail_thread<X>(void* x);
    friend void* search_prime_thread<X>(void* x);

};


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////// Implementation of the Prime Tasker class ///////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

template <class X>
class SubPrimeTask {
  public:
    SubPrimeTask(PrimeTasker<X>* t, size_t idx) : tasker(t), index(idx) {};
    PrimeTasker<X>* tasker;
    size_t index;
};




// Base data for the fast prime number search algorithm
template<class X>
const X PrimeTasker<X>::task_base[] = {1, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 
        67, 71, 73, 79, 83, 89, 97, 101, 103, 107, 109, 113, 121, 127, 131, 137, 139, 
        143, 149, 151, 157, 163, 167, 169, 173, 179, 181, 187, 191, 193, 197, 199}; //47 elements

template<class X>
const X PrimeTasker<X>::prime1050[] = {
        11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 
        59, 61, 67, 71, 73, 79, 83, 89, 97, 101, 103, 
        107, 109, 113, 127, 131, 137, 139, 149, 151, 157, 
        163, 167, 173, 179, 181, 191, 193, 197, 199, 211, 
        223, 227, 229, 233, 239, 241, 251, 257, 263, 269, 
        271, 277, 281, 283, 293, 307, 311, 313, 317, 331, 
        337, 347, 349, 353, 359, 367, 373, 379, 383, 389, 
        397, 401, 409, 419, 421, 431, 433, 439, 443, 449, 
        457, 461, 463, 467, 479, 487, 491, 499, 503, 509, 
        521, 523, 541, 547, 557, 563, 569, 571, 577, 587, 
        593, 599, 601, 607, 613, 617, 619, 631, 641, 643, 
        647, 653, 659, 661, 673, 677, 683, 691, 701, 709, 
        719, 727, 733, 739, 743, 751, 757, 761, 769, 773, 
        787, 797, 809, 811, 821, 823, 827, 829, 839, 853, 
        857, 859, 863, 877, 881, 883, 887, 907, 911, 919, 
        929, 937, 941, 947, 953, 967, 971, 977, 983, 991, 
        997, 1009, 1013, 1019, 1021, 1031, 1033, 1039, 1049}; // 172 numbers;

// Ctor
template<class X>
PrimeTasker<X>::PrimeTasker()
        : next_start(FIRST_BASE), list(prime1050, INIT_PRIME_COUNT), top_p(0),
          mutex(PTHREAD_MUTEX_INITIALIZER), sync_mutex(PTHREAD_MUTEX_INITIALIZER)
{
    pthread_mutex_init(&mutex, NULL);
    pthread_mutex_init(&sync_mutex, NULL);
};



template<class X>
bool PrimeTasker<X>::isPrime(X x) {
    Node<Prime<X>, X>* p = list.header();
    while ((x % p->data.getPrime()) && (p->data.getSqPrime() < x) && (p != list.tailer()))
        p = p->next;
    return (x % p->data.getPrime());
}


// Task 1 thread 1
template<class X>
void* PrimeTasker<X>::searchHead() {
    Node<Prime<X>, X>* p = list.header();
    hp = p->data.getPrime();
    while (task_not_done && (hp <= tp) && (p->data.getSqPrime() < big_num) && (p != list.tailer())) {
        if (big_num % hp == 0) {
                // Check mutex lock, in case that more than one numbers are the factors and compete.
                pthread_mutex_lock(&mutex);
                if (task_not_done) {
                    task_not_done = false;
                    result = hp;
                }
                pthread_mutex_unlock(&mutex);
        } else {
            p = p->next;
            hp = p->data.getPrime();
        }
    }
}


// Task 1 thread 2
template<class X>
void* PrimeTasker<X>::searchTail() {
    Node<Prime<X>, X>* p = list.tailer();
    tp = p->data.getPrime();

    while (task_not_done && (hp <= tp) && (p->data.getSqPrime() > big_num) && (p != list.header())) {
        p = p->prev;
        tp = p->data.getPrime();
    }

    while (task_not_done && (hp <= tp) && (p != list.header())) {
        if (big_num % tp == 0) {
                // Check mutex lock, in case that more than one numbers are the factors and compete.
                pthread_mutex_lock(&mutex);
                if (task_not_done) {
                    task_not_done = false;
                    result = tp;
                }
                pthread_mutex_unlock(&mutex);
        } else {
            p = p->prev;
            tp = p->data.getPrime();
        }
    }
}


// Task 1
template<class X>
void PrimeTasker<X>::runTask1(X n) {
    // Must init hp and tp!
    hp = 0; tp = next_start;
    pthread_t th1, th2;
    pthread_create(&th1, NULL, &(search_head_thread<X>), (void*) this);
    pthread_create(&th2, NULL, &(search_tail_thread<X>), (void*) this);
    pthread_join(th1, NULL);
    pthread_join(th2, NULL);
}



// Task 2 thread
template<class X>
void* PrimeTasker<X>::searchPrime(size_t idx) {
    X num = max_p[idx];
    bool prime = false;
    
    do {
        // Check whether current number is a prime
        prime = isPrime(num);
        if (prime) {
            // If yes, put it into the list.
            list.add(num);
            // Check whether its a factor of the given big_num
            if (big_num % num == 0) {
                // Check mutex lock, in case that more than one numbers are the factors and compete.
                pthread_mutex_lock(&mutex);
                if (task_not_done) {
                    result = num;
                    task_not_done = false;
                }
                pthread_mutex_unlock(&mutex);
            }
        }
        
        if (task_not_done) {
            num += INCREMENT;
            
            // Balance the running speed of different threads. 
            // If some thread grows faster than all other threads, it needs to sleep for a few useconds.
            if (prime && (num > top_p)) {
                //pthread_mutex_lock(&sync_mutex);
                top_p = num;
                //pthread_mutex_unlock(&sync_mutex);
                // cout << "Thread " << idx << " is checking the top prime number " << num << ". Sleep 1 ms" << endl;
                usleep(1000);
            }
        }

    } while (task_not_done && (num * num <= big_num));

    max_p[idx] = num;
}


// Task2
template<class X>
void PrimeTasker<X>::runTask2(X n) {
    size_t i;
    SubPrimeTask<X>* sub_tasks[TASK_COUNT];
    
    // Initialize the task parameters.
    for (i = 0; i < TASK_COUNT; i++) {
        max_p[i] = next_start + (X)(task_base[i]);
        sub_tasks[i] = new SubPrimeTask<X>(this, i);
    }
    
    // Create threads.
    pthread_t ths[TASK_COUNT];
    for (i = 0; i < TASK_COUNT; i++) 
        pthread_create(ths + i, NULL, &(search_prime_thread<X>), (void*)sub_tasks[i]);

    // Wait for the threads to finish
    for (i = 0; i < TASK_COUNT; i++) {
        pthread_join(ths[i], NULL);
        delete sub_tasks[i];
    }
    
    // If task_not_done, that means the big_num itself is a big prime number.
    if (task_not_done) {
        result = big_num;
        task_not_done = false;
    }
    
    // Update the next start: (1) The min{max_p[]} got from all the threads, and (2) find out the ceiling base.
    next_start = max_p[0];
    for (i = 1; i < TASK_COUNT; i++) {
        if (max_p[i] < next_start)
            next_start = max_p[i];
    }
    next_start = next_start - (next_start % INCREMENT);
    // Clear the primes (not sure they are continuous prime numbers) from the end of the list.
    list.clearAfter(next_start);
}


template<class X>
X PrimeTasker<X>::runTask(X n) {
    if (n % 2 == 0) return 2;
    if (n % 3 == 0) return 3;
    if (n % 5 == 0) return 5;
    if (n % 7 == 0) return 7;
    
    result = 0;
    big_num = n;
    task_not_done = true;

    runTask1(n);
    if (task_not_done)
        runTask2(n);

    return result;
}


// Search thread functions, implementations.
template<class X>
void* search_head_thread(void* x) {
    ((PrimeTasker<X>*) x)->searchHead();
}

template<class X>
void* search_tail_thread(void* x) {
    ((PrimeTasker<X>*) x)->searchTail();
}

template<class X>
void* search_prime_thread(void* x) {
    SubPrimeTask<X>* t = (SubPrimeTask<X>*) x;
    t->tasker->searchPrime(t->index);
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////// End of implementation of the Prime Tasker class Template ////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#endif

