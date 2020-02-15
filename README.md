# xls2sql
Excel 2 sql

Read an xls(x) file and for each row build a query with the values in the row.

Usage
---

java Xls2sql [-t] -i _filepath_ -q _query_

Use -t (trim) option to trim the values.

Query format
---

select * from test where col1 = [$0] and col2 = [$1];

[$0] and [$1] indicate the first and the second colums in the input file, you can use an arbitrary number of columns.

If our input file contains two columns with values cAr1 and cBr1 the output will be:

select * from test where col1 = 'cAr1' and col2 = 'cBr1';

Another example with an insert:

Query: insert into mytable (col1, col2) values ([$0], [$1]);

Output: insert into mytable (col1, col2) values ('cAr1', 'cBr1');

TODO
---
* Output to file
* Ignore header
* Library version
* More type support
* Decent exceptions handling
* Decent readme.md
