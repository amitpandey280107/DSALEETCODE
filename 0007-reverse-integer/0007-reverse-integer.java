class Solution {
    public int reverse(int x) {
       int  y =x;
        int Q=0;
        while(x!=0){
        int K  = Q;
        if(K>Integer.MAX_VALUE/10||K<Integer.MIN_VALUE/10){
            return 0;
        }
        Q =x%10;
        Q = K*10+ Q;
        x = x/10;
        }
      
    return Q;
    }
}