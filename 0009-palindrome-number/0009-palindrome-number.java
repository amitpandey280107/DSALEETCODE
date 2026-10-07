class Solution {
    public boolean isPalindrome(int x) {
        int digit = 0;
        int og = x;
        int rem = 0;
        while(x!=0){
            digit = rem;
            rem = x%10;
            x = x/10;
            rem = digit*10+rem;
        }
        if(og==rem&&og>=0){
            return true;
        }
        return false;
    }
}