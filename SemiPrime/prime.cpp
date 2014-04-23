#include <iostream>
#include <stdint.h>
#include "linkedlist.h"
#include "prime_tasker.h"

int main() {
    uint64_t num, factor;
    PrimeTasker<uint64_t> pt;
    do {
        cout << "Please input a number (stop with 0) ";
        cin >> num;
        if (! num) break;

        cout << endl << num << " has factors: 1 ";
        while (num > 1) {
            factor = pt.runTask(num);
            cout << " * " << factor;
            num /= factor;
        }
        cout << endl;
    } while (1);
}
