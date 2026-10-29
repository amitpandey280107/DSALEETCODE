class Solution {
    int n;
    int[] bit;

    void upd(int i, int v) {            // XOR v into position i
        for (i++; i <= n + 1; i += i & -i) bit[i] ^= v;
    }
    int qry(int i) {                    // prefix XOR up to i
        int r = 0;
        for (i++; i > 0; i -= i & -i) r ^= bit[i];
        return r;
    }

    public List<Boolean> palindromePath(int n, int[][] edges, String s, String[] queries) {
        this.n = n;
        bit = new int[n + 2];

        // adjacency list
        int[] head = new int[n], nxt = new int[2 * (n - 1)], to = new int[2 * (n - 1)];
        Arrays.fill(head, -1);
        int cnt = 0;
        for (int[] e : edges) {
            to[cnt] = e[1]; nxt[cnt] = head[e[0]]; head[e[0]] = cnt++;
            to[cnt] = e[0]; nxt[cnt] = head[e[1]]; head[e[1]] = cnt++;
        }

        int LOG = 16;
        int[][] up = new int[LOG][n];
        int[] depth = new int[n], tin = new int[n], size = new int[n], base = new int[n];
        int[] cur = new int[n];
        for (int i = 0; i < n; i++) cur[i] = s.charAt(i) - 'a';

        // iterative DFS (avoids stack overflow on chain-like trees)
        int[] order = new int[n];
        int[] stack = new int[n];
        int sp = 0, timer = 0;
        stack[sp++] = 0;
        up[0][0] = 0;
        base[0] = 1 << cur[0];
        while (sp > 0) {
            int u = stack[--sp];
            tin[u] = timer;
            order[timer++] = u;
            for (int e = head[u]; e != -1; e = nxt[e]) {
                int v = to[e];
                if (v == up[0][u] && u != 0 || (u == 0 && v == 0)) continue;
                if (u != 0 && v == up[0][u]) continue;
                up[0][v] = u;
                depth[v] = depth[u] + 1;
                base[v] = base[u] ^ (1 << cur[v]);
                stack[sp++] = v;
            }
        }
        // subtree sizes
        for (int i = n - 1; i >= 0; i--) {
            int u = order[i];
            size[u] += 1;
            if (u != 0) size[up[0][u]] += size[u];
        }
        for (int k = 1; k < LOG; k++)
            for (int v = 0; v < n; v++)
                up[k][v] = up[k - 1][up[k - 1][v]];

        List<Boolean> ans = new ArrayList<>();
        for (String q : queries) {
            String[] p = q.split(" ");
            if (p[0].equals("update")) {
                int u = Integer.parseInt(p[1]);
                int c = p[2].charAt(0) - 'a';
                int delta = (1 << cur[u]) ^ (1 << c);
                cur[u] = c;
                upd(tin[u], delta);                    // range XOR on [tin[u], tin[u]+size[u]-1]
                upd(tin[u] + size[u], delta);
            } else {
                int u = Integer.parseInt(p[1]), v = Integer.parseInt(p[2]);
                int l = lca(u, v, up, depth, LOG);
                int m = (base[u] ^ qry(tin[u])) ^ (base[v] ^ qry(tin[v])) ^ (1 << cur[l]);
                ans.add((m & (m - 1)) == 0);
            }
        }
        return ans;
    }

    int lca(int u, int v, int[][] up, int[] depth, int LOG) {
        if (depth[u] < depth[v]) { int t = u; u = v; v = t; }
        int diff = depth[u] - depth[v];
        for (int k = 0; k < LOG; k++)
            if ((diff >> k & 1) == 1) u = up[k][u];
        if (u == v) return u;
        for (int k = LOG - 1; k >= 0; k--)
            if (up[k][u] != up[k][v]) { u = up[k][u]; v = up[k][v]; }
        return up[0][u];
    }
}