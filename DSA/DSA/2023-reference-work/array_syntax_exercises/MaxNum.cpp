#include<iostream>
using namespace std;




int main(){
    int arr[]{14,3,4,6,7,41,56,11,25,9},max,min;
    max=arr[0];
    min=arr[0];

    for(int i=0 ; i<10 ; i++){
        if(arr[i] < min){
            min = arr[i];
        }

        if(arr[i] > max){
            max = arr[i];
        }
    }

    cout<<"The maximum number: "<<max<<endl;
    cout<<"The minimum number: "<<min<<endl;
}