#include <chrono>
#include <cstdlib>
#include <iostream>
#include <string>
#include <vector>
#include "JoinQuery.hpp"

//---------------------------------------------------------------------------
int main(int argc, char *argv[])
{
   using namespace std;
   if (argc != 4) {
      cout << "Usage: " << argv[0]
           << " <lineitem.tbl> <order.tbl> <customer.tbl>";
      exit(1);
   }

   string lineitem(argv[1]);
   string orders(argv[2]);
   string customer(argv[3]);
   vector<string> segments;
   // get segments from standard input
   for (string segment; getline(cin, segment);) segments.push_back(segment);

   JoinQuery q(lineitem, orders, customer);
   
   // run a query for each retrieved segment
   for (auto &segment : segments) cout << q.avg(segment) << "\n";

   // flush output buffer
   cout.flush();
}
//---------------------------------------------------------------------------
