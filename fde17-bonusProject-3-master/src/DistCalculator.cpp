#include "../include/DistCalculator.hpp"
#include <chrono>
#include <cstdlib>
#include <iostream>
#include <string>
#include <vector>
#include<list>
#include<queue>
#include<fstream>
#include<sstream>

using namespace std;
std::string lPath;

int fin_count = 0;

//#define INF 2 << 22

int64_t n = 4000000;

class Graph
{
  int64_t V;
  vector<int64_t> *adj;
  
public:
  Graph(int64_t V);
  void addEdge(int64_t v, int64_t w);
  int BFS(int64_t s, int64_t t);
};

Graph g(n);

Graph::Graph(int64_t V)
{
  this->V = V;
  adj = new vector<int64_t>[V];
 
}

void Graph::addEdge(int64_t v, int64_t w) {
  adj[v].push_back(w);
  adj[w].push_back(v);
}

int Graph::BFS(int64_t s, int64_t t) {

    vector<int> dist(n,-1);
    //vector<int64_t> parent(n,-1);

    dist[s] = 0;
    queue<int64_t> Q;
    Q.push( s );


    while( !Q.empty() ){
        int64_t u = Q.front();
        Q.pop();
        

        //vector<int64_t>::iterator it;
        //for (it = adj[u].begin(); it != adj[u].end(); ++it)
        for (auto& node : adj[u])
        {
            if( dist[node] == -1 ){
                Q.push(node);
                dist[node] = dist[u] + 1;
                //parent[*it] = u;
            }
            if(dist[t]!=-1)
                break;
        }
        if(dist[t]!=-1)
            break;
    }

    int count=0;
    if( dist[t] != -1 )
    {
        count = dist[t]/2;
    }
    else
        count=-1;

    return count;
  
}

DistCalculator::DistCalculator(std::string edgeListFile) {
    
    lPath = edgeListFile;

    std::ifstream file(lPath);
    
    string dummyline;
    std::getline(file,dummyline);
        int64_t fir,sec;
	while(file >> fir 
	 && file.ignore(std::numeric_limits<std::streamsize>::max(), ',')
			&& file >> sec){
        	g.addEdge(fir, sec+2000000);
	}
}

int64_t DistCalculator::dist(Node a, Node b)
{
   // TODO: implement distanc calculation
    
    if(a!=b)
    {
        fin_count=g.BFS(a, b);
    }
    else
    {
        fin_count=0;
    }
    return fin_count;
}
