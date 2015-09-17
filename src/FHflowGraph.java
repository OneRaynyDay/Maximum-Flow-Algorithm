import java.util.*;

import cs_1c.Pair;

// --- assumes definition of simple class Pair<E, F> in cis27a

// --- FHflowVertex class ------------------------------------------------------
class FHflowVertex<E> {
   public static Stack<Integer> keyStack = new Stack<Integer>();
   public static final int KEY_ON_DATA = 0, KEY_ON_DIST = 1;
   public static int keyType = KEY_ON_DATA;
   public static final double INFINITY = Double.MAX_VALUE;
   public HashSet<Pair<FHflowVertex<E>, Double>> flowAdjList = 
         new HashSet<Pair<FHflowVertex<E>, Double>>();
   public HashSet<Pair<FHflowVertex<E>, Double>> resAdjList = 
         new HashSet<Pair<FHflowVertex<E>, Double>>();
   public E data;
   public double dist;
   public FHflowVertex<E> nextInPath; // for client-specific info

   public FHflowVertex(E x) {
      data = x;
      dist = INFINITY;
      nextInPath = null;
   }

   public FHflowVertex() {
      this(null);
   }

   public void addToFlowAdjList(FHflowVertex<E> neighbor, double cost) {
      flowAdjList
            .add(new Pair<FHflowVertex<E>, Double>(neighbor, (double) cost));
   }

   public void addToResAdjList(FHflowVertex<E> neighbor, double cost) {
      resAdjList
            .add(new Pair<FHflowVertex<E>, Double>(neighbor, (double) cost));
   }

   public boolean equals(Object rhs) {
      FHflowVertex<E> other = (FHflowVertex<E>) rhs;
      switch (keyType) {
      case KEY_ON_DIST:
         return (dist == other.dist);
      case KEY_ON_DATA:
         return (data.equals(other.data));
      default:
         return (data.equals(other.data));
      }
   }

   public int hashCode() {
      switch (keyType) {
      case KEY_ON_DIST:
         Double d = dist;
         return (d.hashCode());
      case KEY_ON_DATA:
         return (data.hashCode());
      default:
         return (data.hashCode());
      }
   }

   public void showFlowAdjList() {
      Iterator<Pair<FHflowVertex<E>, Double>> iter;
      Pair<FHflowVertex<E>, Double> pair;

      System.out.print("Adj Flow List for " + data + ": ");
      for (iter = flowAdjList.iterator(); iter.hasNext();) {
         pair = iter.next();
         System.out.print(pair.first.data + "("
               + String.format("%3.1f", pair.second) + ") ");
      }
      System.out.println();
   }

   public void showResAdjList() {
      Iterator<Pair<FHflowVertex<E>, Double>> iter;
      Pair<FHflowVertex<E>, Double> pair;

      System.out.print("Adj Res List for " + data + ": ");
      for (iter = resAdjList.iterator(); iter.hasNext();) {
         pair = iter.next();
         System.out.print(pair.first.data + "("
               + String.format("%3.1f", pair.second) + ") ");
      }
      System.out.println();
   }

   public static boolean setKeyType(int whichType) {
      switch (whichType) {
      case KEY_ON_DATA:
      case KEY_ON_DIST:
         keyType = whichType;
         return true;
      default:
         return false;
      }
   }

   public static void pushKeyType() {
      keyStack.push(keyType);
   }

   public static void popKeyType() {
      keyType = keyStack.pop();
   };
}

public class FHflowGraph<E> {
   // the graph data is all here --------------------------
   protected HashSet<FHflowVertex<E>> vertexSet;
   FHflowVertex<E> startVert, endVert;

   // public graph methods --------------------------------
   public FHflowGraph() {
      vertexSet = new HashSet<FHflowVertex<E>>();
   }

   public void addEdge(E source, E dest, double cost) {
      FHflowVertex<E> src, dst;

      // put both source and dest into vertex list(s) if not already there
      src = addToVertexSet(source);
      dst = addToVertexSet(dest);

      // add dest to source's adjacency list
      src.addToResAdjList(dst, cost);
      src.addToFlowAdjList(dst, 0);

      dst.addToResAdjList(src, 0);
   }

   public void addEdge(E source, E dest, int cost) {
      addEdge(source, dest, (double) cost);
   }

   // adds vertex with x in it, and always returns ref to it
   public FHflowVertex<E> addToVertexSet(E x) {
      FHflowVertex<E> retVal, vert;
      boolean successfulInsertion;
      Iterator<FHflowVertex<E>> iter;

      // save sort key for client
      FHflowVertex.pushKeyType();
      FHflowVertex.setKeyType(FHflowVertex.KEY_ON_DATA);

      // build and insert vertex into master list
      retVal = new FHflowVertex<E>(x);
      successfulInsertion = vertexSet.add(retVal);

      if (successfulInsertion) {
         FHflowVertex.popKeyType(); // restore client sort key
         return retVal;
      }

      // the vertex was already in the set, so get its ref
      for (iter = vertexSet.iterator(); iter.hasNext();) {
         vert = iter.next();
         if (vert.equals(retVal)) {
            FHflowVertex.popKeyType(); // restore client sort key
            return vert;
         }
      }

      FHflowVertex.popKeyType(); // restore client sort key
      return null; // should never happen
   }

   public void showFlowAdjTable() {
      Iterator<FHflowVertex<E>> iter;

      System.out.println("-------FlowAdjTable-------- ");
      for (iter = vertexSet.iterator(); iter.hasNext();)
         (iter.next()).showFlowAdjList();
      System.out.println();
   }

   public void showResAdjTable() {
      Iterator<FHflowVertex<E>> iter;

      System.out.println("---------ResAdjTable-------- ");
      for (iter = vertexSet.iterator(); iter.hasNext();)
         (iter.next()).showResAdjList();
      System.out.println();
   }

   public HashSet<FHflowVertex<E>> getVertSet() {
      return (HashSet<FHflowVertex<E>>) vertexSet.clone();
   }

   public void clear() {
      vertexSet.clear();
   }

   public boolean setStartVert(E x) {
      startVert = getVertexWithThisData(x);
      if (startVert == null)// vertex not found
         return false;
      return true;
   }

   public boolean setEndVert(E x) {
      endVert = getVertexWithThisData(x);
      if (endVert == null)// vertex not found
         return false;
      return true;
   }

   protected FHflowVertex<E> getVertexWithThisData(E x) {
      FHflowVertex<E> searchVert, vert;
      Iterator<FHflowVertex<E>> iter;

      // save sort key for client
      FHflowVertex.pushKeyType();
      FHflowVertex.setKeyType(FHflowVertex.KEY_ON_DATA);

      // build vertex with data = x for the search
      searchVert = new FHflowVertex<E>(x);

      // the vertex was already in the set, so get its ref
      for (iter = vertexSet.iterator(); iter.hasNext();) {
         vert = iter.next();
         if (vert.equals(searchVert)) {
            FHflowVertex.popKeyType();
            return vert;
         }
      }

      FHflowVertex.popKeyType();
      return null; // not found
   }

   // added methods---------------------------------------------------
   public double findMaxFlow() {
      while (establishNextFlowPath()) {
         adjustPathByCost(getLimitingFlowOnResPath());
      }
      Iterator<Pair<FHflowVertex<E>, Double>> edgeIter;
      double maxFlow = 0;
      for (edgeIter = startVert.flowAdjList.iterator(); edgeIter.hasNext();) {
         maxFlow += edgeIter.next().second;
      }
      return maxFlow;
   };

   private boolean establishNextFlowPath() {
      FHflowVertex<E> w, s, v;
      Pair<FHflowVertex<E>, Double> edge;
      Iterator<FHflowVertex<E>> iter;
      Iterator<Pair<FHflowVertex<E>, Double>> edgeIter;
      Double costVW;
      Deque<FHflowVertex<E>> partiallyProcessedVerts = new LinkedList<FHflowVertex<E>>();

      if (startVert == null || endVert == null)
         return false;

      // initialize the vertex list and place thestartVerttarting vert in p_p_v
      // queue
      for (iter = vertexSet.iterator(); iter.hasNext();)
         iter.next().dist = FHflowVertex.INFINITY;

      startVert.dist = 0;
      partiallyProcessedVerts.addLast(startVert);

      // outer dijkstra loop
      while (!partiallyProcessedVerts.isEmpty()) {
         v = partiallyProcessedVerts.removeFirst();

         // inner dijkstra loop: for each vert adj to v, lower its dist
         // tostartVert if you can
         for (edgeIter = v.resAdjList.iterator(); edgeIter.hasNext();) {
            edge = edgeIter.next();
            w = edge.first;
            costVW = edge.second;
            // cost of 0 means the path cannot move forward
            if (costVW == 0)
               continue;
            if (v.dist + costVW < w.dist) {
               w.dist = v.dist + costVW;
               w.nextInPath = v;
               // w now has improved distance, so add w to PPV queue
               partiallyProcessedVerts.addLast(w);
            }

            if (w == endVert)
               return true;
         }
      }
      return false;
   }

   // establishNextFlowPath() already guarrantees a dijkstra iteration
   private double getLimitingFlowOnResPath() {
      FHflowVertex<E> vert = endVert;
      double minCap = FHflowVertex.INFINITY;
      Stack<FHflowVertex<E>> pathStack = new Stack<FHflowVertex<E>>();

      if (startVert == null || endVert == null)
         return 0;

      // perhaps add argument opting to skip if pre-computed
      if (endVert.dist == FHflowVertex.INFINITY) {
         return 0;
      }
      while (vert != startVert) {
         double cost = getCostOfResEdge(vert, vert.nextInPath);
         if (minCap > cost) {
            minCap = cost;
         }
         vert = vert.nextInPath;
      }
      // now that we have vertices, find min
      return minCap;
   }

   private boolean adjustPathByCost(double cost) {
      FHflowVertex<E> vert = endVert;
      while (vert != startVert) {
         if (!addCostToResEdge(vert, vert.nextInPath, cost)
               || !addCostToFlowEdge(vert, vert.nextInPath, cost))
            throw new IllegalStateException();
         vert = vert.nextInPath;
      }
      return false;
   }

   private boolean addCostToResEdge(FHflowVertex<E> src, FHflowVertex<E> dst,
         double cost) {
      Iterator<Pair<FHflowVertex<E>, Double>> edgeIter;
      Pair<FHflowVertex<E>, Double> edge;
      // did initial/final dec/increment?
      boolean iDec = false, fInc = false;
      for (edgeIter = dst.resAdjList.iterator(); edgeIter.hasNext();) {
         edge = edgeIter.next();
         // we have found it!
         if (edge.first.equals(src)) {
            edge.second -= cost;
            iDec = true;
         }
      }
      for (edgeIter = src.resAdjList.iterator(); edgeIter.hasNext();) {
         edge = edgeIter.next();
         // we have found it!
         if (edge.first.equals(dst)) {
            edge.second += cost;
            fInc = true;
         }
      }
      return (iDec && fInc);
   }

   // src is the vertex closer to endVert
   private double getCostOfResEdge(FHflowVertex<E> src, FHflowVertex<E> dst) {
      Iterator<Pair<FHflowVertex<E>, Double>> edgeIter;
      Pair<FHflowVertex<E>, Double> edge;
      for (edgeIter = dst.resAdjList.iterator(); edgeIter.hasNext();) {
         edge = edgeIter.next();
         if (edge.first.equals(src))
            return edge.second;
      }
      return 0;// if it reached here, there is a problem
   }

   private boolean addCostToFlowEdge(FHflowVertex<E> src, FHflowVertex<E> dst,
         double cost) {
      Iterator<Pair<FHflowVertex<E>, Double>> edgeIter;
      Pair<FHflowVertex<E>, Double> edge;
      // did initial/final dec/increment?
      boolean iDec = false, fInc = false;
      // forwards - theoretically should work
      for (edgeIter = dst.flowAdjList.iterator(); edgeIter.hasNext();) {
         edge = edgeIter.next();
         if (edge.first.equals(src)) {
            edge.second += cost;
            iDec = true;
         }
      }
      // that means we have already changed the flow graph - no need to
      // continue.
      if (iDec)
         return true;
      for (edgeIter = src.flowAdjList.iterator(); edgeIter.hasNext();) {
         edge = edgeIter.next();
         // we have found it!
         if (edge.first.equals(dst)) {
            edge.second -= cost;
            fInc = true;
         }
      }
      if (fInc)
         return true;
      else
         return false;
   }
}
