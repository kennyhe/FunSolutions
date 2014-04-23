#ifndef __SHE_LINKED_LIST_H__
    #define __SHE_LINKED_LIST_H__
#include <pthread.h>

using namespace std;

template <class X>
class Prime {
  private:
    X prime;
    X sq_prime;
  public:
    Prime (X n):prime(n),sq_prime(n*n) {};
    X getPrime() { return prime; };
    X getSqPrime() { return sq_prime; };
};

template <class T, class X>
class Node {
  public:
    T data;
    Node* prev;
    Node* next;
    Node (X n):data(n) {};
};


template <class T, class X>
class LL {
  private:
    Node<T,X> head;
    Node<T,X> rear;
    void clearAfter(Node<T,X>* p);
    pthread_mutex_t mutex;
    void init();
  public:
    LL();
    LL(const X* p, const size_t len);
    ~LL();
    void add(X p);
    Node<T,X>* header() {return head.next;}
    Node<T,X>* tailer() {return rear.prev;}
    void clearAll();
    void clearAfter(X n);
};

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////// Implementation of the Linked List class ///////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////

template<class T, class X>
void LL<T,X>::init() {
    // Init the mutex to prevent multiple writing
    pthread_mutex_init(&mutex, NULL);
    rear.prev = &head;
    head.next = &rear;
}
    
template<class T, class X>
LL<T,X>::LL():head(0), rear(0), mutex(PTHREAD_MUTEX_INITIALIZER) {
    init();
}


template<class T, class X>
LL<T,X>::LL(const X* p, const size_t len) 
        : head(0), rear(0), mutex(PTHREAD_MUTEX_INITIALIZER) {
    init();
    for (size_t i = 0; i < len; i++) {
        add(p[i]);
    }
}


template<class T, class X>
LL<T,X>::~LL<T,X>() {
    clearAfter(&head);
}


template<class T, class X>
void LL<T,X>::add(X n) {
    pthread_mutex_lock(&mutex);
    Node<T,X>* p = rear.prev;
    if (p != &head) { // Handle the initial case, when the list is empty.
        while (p->data.getPrime() > n)
            p = p->prev;
    }
    
    Node<T,X>* node = new Node<T,X>(n);
    node->next = p->next;
    p->next->prev = node;
    node->prev = p;
    p->next = node;
    pthread_mutex_unlock(&mutex);
}


template<class T, class X>
void LL<T,X>::clearAll() {
    clearAfter(&head);
}
    

template<class T, class X>
void LL<T,X>::clearAfter(Node<T,X>* pos) {
    pthread_mutex_lock(&mutex);
    Node<T,X> *p = pos->next, *q;
    while (p != &rear) {
        q = p->next;
        delete p;
        p = q;
    }
    pos->next = &rear;
    rear.prev = pos;
    pthread_mutex_unlock(&mutex);
}


template<class T, class X>
void LL<T,X>::clearAfter(X n) {
    Node<T,X>* p = rear.prev;
    while (p->data.getPrime() > n)
        p = p->prev;
    clearAfter(p);
}
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////// End of implementation of the Linked List class Template ////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////

#endif

