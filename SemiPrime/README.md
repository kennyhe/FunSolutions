Prime numbers discovery and semi-prime problem solving
======================================================
This is a solution to the semi-prime problem with an improved algorithm and C++ templates, pthreads. It runs fast and efficient and can handle big numbers exceed the limitation of UNIT64_MAX. 

The requirements is described in the document p1.pdf.

For a given big semi-prime number, my program will:

First try to discover the prime numbers range from 2 to sqrt(N), and divide N with each prime number found. If can be divided, then check whether another number is also prime number.

Finally my program will give all the prime factors of the given number N. If it is not a semi-prime, my program will give all the prime factors.


Prime number discovery algorithm
================================
The commonly used algorithm for checking whether a small number is prime is:
<pre>
bool isPrime(N) {
  for i = 2 to N-1 {
   if (N mod i == 0) return false;
  }
  return true;
}
Complexity: O(N)
</pre>

An optimized version:
<pre>
bool isPrime(N) {
  if (N mod 2 == 0) return false;
  i = 3; // then only check the odd numbers less than sqrt(N)
  while (i * i &lt; N) {
    if (N mod i == 0) return false;
    i += 2;
  }
  return true;
}
Complexity: sqrt(N) / 2
</pre>

If we need to find all prime numbers from 2 to N, the best solution is: Store the known prime numbers. For each big number, use it to divide the known prime numbers, until the square of the known prime number is bigger than that big number. If such big number is a prime number, append it to the prime numbers list.

However, we need not check the big numbers at the increment of 1 or 2. Apparently we can skip some numbers that we are sure that they can be divided by small prime numbers such as 3, 5, 7.

As we know, 2 * 3 * 5 * 7 = 210, so for the numbers between every (210 * n) and (210 * n + 209), (n>=1) we can skip all the (210 * n + 2 * k), (210 * n + 3 * l), (210 * n + 5 * p), and (210 * n + 7 * q). Here k, l, p, q are positive integers. Then we can find all the m that we are sure that (210 * n + m) can not be divied by the 2, 3, 5, 7:
<pre>
1, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101, 103, 107, 109, 113, 121, 127, 131, 137, 139, 143, 149, 151, 157, 163, 167, 169, 173, 179, 181, 187, 191, 193, 197, 199
</pre>

There are totally 47 possible m numbers.


Discover the prime numbers in parallel
======================================
So I think why not let the computer to discover the prime numbers in parallel? My program created 47 threads, each thread pick a "m" number in the list above and check, then increment by 210. The discovered prime numbers will be added into the prime numbers list in order. At the beginning, I put enough known prime numbers in the list (in my implementation, I put all known prime numbers between 2 and 1050 = 210 * 5), to ensure that the known prime numbers list is long enough for discovering the new prime numbers.


Keep the threads synchronized
=============================
As we know, the prime numbers are not evenly spreaded in the 47 branches. So the checked numbers in some threads increase faster than those in other threads, which may be harmful for the stability of the algorithm. Then I used some global variables to watch the numbers in each thread. If the checked number in a thread increases too fast, my program will let that thread to wait for a couple of microseconds.

I also created a mutex to control the writing discovered prime numbers to known prime numbers list. Only one thread can get the mutex and write at one time so that we can guarantee the numbers in the list will be corrected ordered.


Check multiple semi-prime numbers
=================================
If we need to check more than one numbers(e.g. sp-1, sp-2, ...., sp-x), we can share the same known prime numbers list (p1 = 2, P2 = 3, p3 = 5, ...., p-y) without discovering them again. If sp-k (1 <= k <= x) is not bigger than square of p-y, then definitely we can quickly check whether it is a semi-prime number. Else, we need to discover more prime numbers to make sure the max known prime number p-y is bigger than sqrt(sp-k).

However, as I mentioned above, the numbers in different threads increase in various speed. So there may be some "holes" of prime numbers in the list. In other words, when the threads are running, we can not guarantee that the prime numbers in the prime numbers list are consecutive: Some prime numbers which are less than the max known prime number p-y may have not been discovered yet when the threads are running!

We can only make sure that the prime numbers before the p-m = MIN(z, zЄ{ max known prime number discovered in each thread }) are consecutive. If p-m is bigger than sqrt(sp-k), we can make sure that whether sp-k is a prime number or not, also we can make sure whether it is a semi-prime number. Then we can suspend all the threads, delete the numbers bigger than p-m from the prime numbers list, and record the next start point (210 * M) of the prime numbers discovery.


Limitations and future optimizations
====================================
One limitation of my program is that it can only process the unsigned 64 bit integers (uint64_t), that means the biggest prime number my program can handle is less than sqrt(UINT64_MAX, 18446744073709551615). Absolutely it is not enough for the industrial applications.

So in my implementation, I use the template for all elemental data. If needs to handle the big integers beyond the limitation of UINT64_MAX, we can implement a data type which support all the int operations to replace uint64_t.

Additionally, I found that the computational limitation is also a bottleneck of my program. When it handles a 18 digit integer, it needs a long time to compute. Maybe we can use some more powerful distributed computing system to solve this problem.

About the prime number discovering algorithm, there may be no much space for improvement. The complexity of my algorithm is sqrt(N) * 47 / 210, and the running time is sqrt(N) * 47 / 210 / (count of CPU cores). If we consider skip the numbers can be divided by 11, 13, 17, 19, maybe we can make some improvement but not so significantly as we improved from skipping the dividents by 3, 5, and 7 (reduced from 1/2 to 2/6, 8/30, 47/210). And the increasing of the threads count will cause extra payloads which may descrease the overall performance. So the current implementation is almost the most optimal solution in general.
