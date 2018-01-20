package eu.eyan.graph

import eu.eyan.graph.impl.GraphEdgeUndirected
import eu.eyan.graph.impl.GraphEdgeDirected
import eu.eyan.graph.impl.GraphImplSimple

/* 
 * This description comes from wikipedia: https://en.wikipedia.org/wiki/Graph_(discrete_mathematics)
 * I am going to use the naming of this site.
 *  
 * Graph G = (V, E) comprising a set 
 * 	 - V of vertices, nodes or points together with a set 
 *   - E of edges, arcs or line
 *   - A vertex may exist in a graph and not belong to an edge.
 * Undirected graph
 * Simple graph
 * 
 * Multigraphs 
 * Pseudographs
 * 
 * Order of a graph is |V|, its number of vertices. 
 * Size of a graph is |E|, its number of edges. 
 * Degree or valency of a vertex is the number of edges that connect to it, where an edge that connects to the vertex at both ends (a loop) is counted twice.
 * 
 * Adjacency relation (minek?)
 * 
 * Types of graphs
 * 
 * Undirected simple finite graph
 * 
 * Undirected graph
 *   - The maximum number of edges in an undirected graph without a loop is n(n − 1)/2.
 *   
 * Directed graph
 *   - successor
 *   - predecessor
 *   - reachable
 *   - inverted arrow: The arrow (y, x) is called the inverted arrow of (x, y).
 *   - symmetric if, for every arrow in G, the corresponding inverted arrow also belongs to G.
 *   
 * Oriented graph
 *   - An oriented graph is a directed graph in which at most one of (x, y) and (y, x) may be arrows of the graph. 
 *   
 * Mixed graph
 *   - A mixed graph is a graph in which some edges may be directed and some may be undirected. 
 *   - It is written as an ordered triple G = (V, E, A) 
 *   
 * Multigraph
 *   - Multiple edges are two or more edges that connect the same two vertices. 
 *   - Loop is an edge (directed or undirected) that connects a vertex to itself; it may be permitted or not, according to the application. 
 *   - In this context, an edge with two different ends is called a link.
 *   - multigraph is often defined to mean a graph without loops
 *   
 * Pseudograph
 *    - graph which can have both multiple edges and loops,[6] although many use the term pseudograph for this meaning.
 *    
 * Simple graph
 *   - an undirected graph in which both multiple edges and loops are disallowed. 
 *   - In a simple graph with n vertices, the degree of every vertex is at most n − 1.
 *   
 * Quiver
 *   - quiver or multidigraph is a directed multigraph. A quiver may have directed loops in it. 
 * 
 * Weighted graph
 *   - a graph in which a number (the weight) is assigned to each edge.
 *   - some authors call such a graph a network.
 * 
 * Half-edges, Loose edges
 *   - In certain situations it can be helpful to allow edges with only one end, called half-edges, or no ends, called loose edges; 
 *   - see the articles Signed graphs and Biased graphs.
 *   
 * 
 * Important classes of graph
 *   - Regular graph
 *     - A regular graph is a graph in which each vertex has the same number of neighbours, i.e., every vertex has the same degree. 
 *     - k-regular: A regular graph with vertices of degree k is called a k-regular graph or regular graph of degree k.
 *     
 *   - Complete graph
 *     - A complete graph is a graph in which each pair of vertices is joined by an edge. A complete graph contains all possible edges.
 *   
 *   - Finite graph
 *     - A finite graph is a graph in which the vertex set and the edge set are finite sets. Otherwise, it is called an infinite graph.
 *   
 *   - Connected graph
 *     - Connected vertices: In an undirected graph, an unordered pair of vertices {x, y} is called connected if a path leads from x to y. 
 *     - Disconnected: Otherwise, the unordered pair is called disconnected.
 *     - Connected graph is an undirected graph in which every unordered pair of vertices in the graph is connected
 *     - Disconnected graph: Otherwise, it is called a disconnected graph.
 *     - Strongly connected pairs: In a directed graph, an ordered pair of vertices (x, y) is called strongly connected if a directed path leads from x to y. 
 *     - Weakly connected pairs: the ordered pair is called weakly connected if an undirected path leads from x to y after replacing all of its directed edges with undirected edges. 
 *     - Strongly connected graph is a directed graph in which every ordered pair of vertices in the graph is strongly connected. 
 *     - Weakly connected graph if every ordered pair of vertices in the graph is weakly connected. 
 *     
 *   - K-vertex-connected graph 
 *     - k-edge-connected graph is a graph in which no set of k − 1 vertices (respectively, edges) exists that, when removed, disconnects the graph. 
 *     
 *   - Bipartite graph
 *     - A bipartite graph is a graph in which the vertex set can be partitioned into two sets, W and X, 
 *       so that no two vertices in W share a common edge and no two vertices in X share a common edge. 
 *     - Alternatively, it is a graph with a chromatic number of 2.
 *   - Complete bipartite graph
 *     -  the vertex set is the union of two disjoint sets, W and X, so that every vertex in W is adjacent to every vertex in X but there are no edges within W or X.
 *     
 *   - Path graph
 *     - A path graph or linear graph of order n ≥ 2 is a graph in which the vertices can be listed in an order v1, v2, …, vn such that the edges are the {vi, vi+1} where i = 1, 2, …, n − 1. 
 *     - Path graphs can be characterized as connected graphs in which the degree of all but two vertices is 2 and the degree of the two remaining vertices is 1. 
 *   - Path
 *     - if a path graph occurs as a subgraph of another graph, it is a path in that graph.
 *     
 *   - Planar graph
 *     - A planar graph is a graph whose vertices and edges can be drawn in a plane such that no two of the edges intersect.
 *     
 *   - Cycle graph
 *     - A cycle graph or circular graph of order n ≥ 3 is a graph in which the vertices can be listed in an order v1, v2, …, vn such that the edges are the {vi, vi+1} 
 *     - where i = 1, 2, …, n − 1, plus the edge {vn, v1}. 
 *     - Cycle graphs can be characterized as connected graphs in which the degree of all vertices is 2. 
 *     
 *   - Cycle / circuit
 *     - If a cycle graph occurs as a subgraph of another graph, it is a cycle or circuit in that graph.
 *     
 *   - Tree
 *     - is a connected graph with no cycles.
 *     
 *   - Forest
 *     - is a graph with no cycles, i.e. the disjoint union of one or more trees.
 *     
 *   - Petersen graph and its generalizations;
 *   - perfect graphs;
 *   - cographs;
 *   - chordal graphs;
 *   - other graphs with large automorphism groups: vertex-transitive, arc-transitive, and distance-transitive graphs;
 *   - strongly regular graphs and their generalizations distance-regular graphs.
 *   
 *   
 *   
 *   
 *  
 * Properties of graphs[edit]
 *  - Adjecent edges
 *    - Two edges of a graph are called adjacent if they share a common vertex. 
 *  - Consecutive edges
 *    - Two arrows of a directed graph are called consecutive if the head of the first one is the tail of the second one. 
 *  - Adjacent vertices
 *    - two vertices are called adjacent if they share a common edge 
 *  - Consecutive vertices
 *    - if the first one is the tail and the second one is the head of an arrow), in which case the common edge is said to join the two vertices. 
 *  - Incident
 *    - An edge and a vertex on that edge are called incident.
 *    
 *  - Trivial graph
 *    - The graph with only one vertex and no edges is called the trivial graph.
 *  - Edgeless 
 *    - A graph with only vertices and no edges is known as an edgeless graph. 
 *  - Null / empty graph
 *    - The graph with no vertices and no edges is sometimes called the null graph or empty graph, but the terminology is not consistent and not all mathematicians allow this object.
 *  
 *  - Vertex-labeled / edge-labeled graph / unlabeled
 *    - Normally, the vertices of a graph, by their nature as elements of a set, are distinguishable. This kind of graph may be called vertex-labeled.
 *    -  However, for many questions it is better to treat vertices as indistinguishable. 
 *       (Of course, the vertices may be still distinguishable by the properties of the graph itself, e.g., by the numbers of incident edges.) 
 *       The same remarks apply to edges, so graphs with labeled edges are called edge-labeled. 
 *       Graphs with labels attached to edges or vertices are more generally designated as labeled. 
 *       Consequently, graphs in which vertices are indistinguishable and edges are indistinguishable are called unlabeled. 
 *       (Note that in the literature, the term labeled may apply to other kinds of labeling, besides that which serves only to distinguish different vertices or edges.)
 *       
 *     The category of all graphs is the slice category Set ↓ D where D: Set → Set is the functor taking a set s to s × s.
 *     
 *  - morphisms / digraph morphisms
 *    - In category theory, a small category can be represented by a directed multigraph in which the objects of the category are represented as vertices and the morphisms as directed edges.
 *      Then, the functors between categories induce some, but not necessarily all, of the digraph morphisms of the graph.
 *  - conceptual graph
 *    - In computer science, directed graphs are used to represent knowledge (e.g., conceptual graph), finite state machines, and many other discrete structures.
 *  - Cayley graphs
 *  - Schreier coset graphs
 *    - Particularly regular examples of directed graphs are given by the Cayley graphs of finitely-generated groups, as well as Schreier coset graphs
 *    
 *    
 *    
 * - Graph operations
 *   - unary operations
 *     which create a new graph from an initial one
 *     - edge contraction
 *     - line graph
 *     - dual graph
 *     - complement graph
 *     - graph rewriting
 *   - binary operations
 *     which create a new graph from two initial ones
 *     - disjoint union of graphs
 *     - cartesian product of graphs
 *     - tensor product of graphs
 *     - strong product of graphs
 *     - lexicographic product of graphs
 *     - series-parallel graphs.
 *     
 * Generalizations
 *   - Hypergraph
 *     - In a hypergraph, an edge can join more than two vertices.
 *     
 *   - Simplices
 *     An undirected graph can be seen as a simplicial complex consisting of 1-simplices (the edges) and 0-simplices (the vertices). 
 *     As such, complexes are generalizations of graphs since they allow for higher-dimensional simplices.
 *   - Matroids
 *     Every graph gives rise to a matroid.
 *     
 * Notes
 *   - In model theory, a graph is just a structure. But in that case, there is no limitation on the number of edges: it can be any cardinal number, see continuous graph.
 *   - In computational biology, power graph analysis introduces power graphs as an alternative representation of undirected graphs.
 *   - In geographic information systems, geometric networks are closely modeled after graphs, 
 *     and borrow many concepts from graph theory to perform spatial analysis on road networks or utility grids.
 *     
 * See also
 *   - Graph database
 *   - Graph drawing
 *   - Graph theory
 *   - ...
 */

trait GraphDirection
case object GraphDirectionUndirected extends GraphDirection
case object GraphDirectionDirected extends GraphDirection
case object GraphDirectionMixed extends GraphDirection

class GraphUndirectedEdgeNotSupported extends Exception
class GraphDirectedEdgeNotSupported extends Exception
class GraphVertexNotFound extends Exception

/** This is the trait for a simple (undirected) graph. */
trait Graph[VERTEX, EDGE] {

  /** Syntactic sugar: the type of this trait. */
  type GRAPH = Graph[VERTEX, EDGE]
  
  /** Syntactic sugar: the type of more vertices. */
  type VERTICES = Iterable[VERTEX]

  /** Syntactic sugar: the type of more edges. */
  type EDGES = Iterable[GraphEdge[VERTEX]]

  /** Returns all the vertices of the graph. */
  def vertices: Iterable[VERTEX]

  /** Returns all the edges of the graph. */
  def edges: EDGES

  /** Adds a vertex to the graph. If the vertex was already in the graph nothing happens. */
  def add(vertex: VERTEX): GRAPH

  /** Returns the direction type of the graph: Undirected, directed or mixed*/
  def direction: GraphDirection
  
  /** true if the graph supports undirected edges */
  def undirected = direction == GraphDirectionUndirected || direction == GraphDirectionMixed

  /** true if the graph supports directed edges */
  def directed = direction == GraphDirectionDirected || direction == GraphDirectionMixed
  
  /** true if the graph supports both undirected and directed edges */
  def mixed = direction == GraphDirectionMixed
  
  /** Adds an edge to the graph according the properties of the graph. */
  @throws(classOf[GraphUndirectedEdgeNotSupported])
  @throws(classOf[GraphDirectedEdgeNotSupported])
  def add(edge: GraphEdge[VERTEX]): GRAPH
  
  /** Syntactic sugar for creating an undirected edge. */
  @throws(classOf[GraphUndirectedEdgeNotSupported])
  def addUndirectedEdge(vertex1: VERTEX, vertex2: VERTEX): GRAPH

  /** Syntactic sugar for creating a directed edge. */
  @throws(classOf[GraphDirectedEdgeNotSupported])
  def addDirectedEdge(vertex1: VERTEX, vertex2: VERTEX): GRAPH

  /** Returns all the edges of the vertex in the graph. */
  def edges(vertex: VERTEX): EDGES
		  
  /** Returns all the edges of the vertex in the graph. */
  def edges(vertices: VERTICES): EDGES
  
  /** Adds more edge to the graph according the properties of the graph. */
  @throws(classOf[GraphUndirectedEdgeNotSupported])
  @throws(classOf[GraphDirectedEdgeNotSupported])
  def addAll(edges: EDGES): GRAPH
  
  /** The order of a graph is its number of vertices. */
  def order: Int

  /** The size of a graph is its number of edges. */
  def size: Int

  /** The degree or valency of a node is the number of edges that connect to it, where an edge that connects to the node at both ends (a loop) is counted twice. */
  def degree(node: VERTEX): Int
}


/* 
 * TO DEFINE / IMPLEMENT:
Graph[V, E] (vertices, undirectedEdges, directedEdges) (hasUndirectedEdges, hasDirectedEdges , hasMultiEdges, hasLoops, isHyper /*, half edge-loose edge, isFinite*/)
 - remove(v)
 - remove(e)
 - contains(e)
 - contains(v)
 - order(Graph)
 - size(Graph)
 - degree(Vertex)
 - isAdjacent(edge1, edge2)
 - isAdjacent(vertex1, vertex2)
 - isConsecutive(e1,e2)
 - isConsecutive(v1,v2)
 - isIncident(v,e)
 - isComplete(Graph)
 - isSuccessor(vertex1, vertex2)
 - isSuccessor(edge1, edge2)?
 - isPredecessor(vertex1, vertex2)
 - isPredecessor(edge1, edge2)?
 - isReachable(vertex1, vertex2)
 - isSymmetric(Graph)
 - isRegular(Graph)
 - isRegular(k: Int)
 - isConnected(v1, v2): Path?
 - isDisconnected(v1,v2): boolean
 - isConnected(Graph)
 - isDisconnected(Graph)
 - isConnectedStrongly(v1,v2): Path?
 - isConnectedWeakly(v1,v2): Path?
 - isConnectedStrongly(Graph)
 - isConnectedWeakly(Graph)
 - isConnected(Graph)(k:Int) -> k-connected-graph
 - isBipatrite(Graph)
 - isChromatic(k: Int):Groups[Vertices] -> does it make sense?
 - getChromaticNumber(Graph): Int -> does it make sence?
 - isBipartiteComplete : boolean
 - isChromaticComplete(k: Int)
 - isPath(Graph):Path
 - isPath(Vertices): Path -> subGraph?
 - isPath(Edges): Path -> subGraph?
 - isPlanar(Graph)
 - isCycle(Graph)
 - isCycle(Vertices) -> subGraph?
 - isCycle(Edges) -> subGraph?
 - getSubGraph(Vertices)
 - getSubGraph(Edges)
 - isTree(Graph)
 - isForest(Graph):Graphs
 - isTrivial(Graph)
 - isEdgeless(Graph)
 - isEmpty(Graph)
 - nonEmpty(Graph)
 
 - edgeContraction(Graph):Graph
 - lineGraph(Graph):Graph
 - dualGraph(Graph):Graph
 - coplementGraph(Graph):Graph
 - rewriting(Graph):Graph
 - unionDisjoint(g1,g2): Graph
 - productCartesian(g1,g2): Graph
 - productTensor(g1,g2): Graph
 - productStrong(g1,g2): Graph
 - productLexicographic(g1,g2): Graph
 - productseriesParallel(g1,g2): Graph
 

 
 
Morphism

 
Vertex-Labeled / Edge-Labeled / Unlabeled
make is sense to implement vertex-UNlabeled?
-> this makes all complicated.
 
 
 

 

Simple graph = Graph(undirected, no directed, no multi, no loops)
Directed graph = Graph(no undirected, directed, no multi, no loops)
Mixed graph = Graph(undirected, directed, no multi, no loops)
Multigraph = Graph(undirected, directed, multi, no loops)
Pseudograph = Graph(undirected, directed, multi, loops)
Oriented graph =  = Graph(no undirected, directed, no multi, no loops)
Multigraph = Graph(no undirected, directed, multi, loops)

Later:
- Petersen graph and its generalizations;
- perfect graphs;
- cographs;
- chordal graphs;
- other graphs with large automorphism groups: vertex-transitive, arc-transitive, and distance-transitive graphs;
- strongly regular graphs and their generalizations distance-regular graphs.
- Cayley
- Schreier coset
- Simplices
- Matroids
 *  
 */

