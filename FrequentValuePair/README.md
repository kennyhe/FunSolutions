Frequent Value Pair Problem
===========================

This is a response to an algorithm question posted on mitbbs.com:
http://www.mitbbs.com/article_t/CS/31215183.html


Original problem (Translated into English):
Given a maxtrix, N rows, M columns. The values in the matrix are either 1 or 0.
Find out all column pairs (c1, c2), the number of rows with both columns valued "1" are more than a given number X.

I solved this problem by:
Step 1) Find the count of 1 in each column( complexity: O(M*N)), and sort the count in descending order with column id (complexity: O(M^2)); Ignore those columns has less then X "1" values.
Step 2) Build a Frequency Pattern Tree. Complexity: O(M*N)
Step 3) Visit the Frequency Pattern Tree and calculate the frequency: Worst case: O(M^2*N); average case: O(M*N); Best case: O(M).


Reference: 
Mining Frequent Patterns without Candidate Generation: A Frequent-Pattern Tree Approach
Jiawei Han, Jian Pei, Yiwen Yin, Runying Mao
http://link.springer.com/article/10.1023/B:DAMI.0000005258.31418.83
