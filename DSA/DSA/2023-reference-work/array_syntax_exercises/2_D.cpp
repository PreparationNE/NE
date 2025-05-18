#include<iostream>
using namespace std;


void display(int marks[][3],int r , int c){
    for(int i = 0; i < r; i++){
        for(int j=0; j<c;j++){
            cout <<marks[i][j] << " ";
        }
        cout<<endl;
    }
}


int main(){
    int A[3][3] =  {
        {12, 14,12},
        {15, 18,22},
        {15, 18,22}};
    
    display(A,3,3);
}