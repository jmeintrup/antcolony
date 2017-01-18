import org.junit.Test;

import java.util.*;
import java.util.concurrent.*;

/**
 * Created by yay on 22.12.2016.
 */
public class ConcurrencyTest {
    @Test
    public void testConcurrency() {
        Map<Integer, Node> map = new HashMap<>();
        Node[] nodes = new Node[]{
                new Node(1,1),
                new Node(2,2),
                new Node(3,3),
                new Node(3,6)
        };
        for(int i = 0; i < nodes.length; i++) {
            map.put(i, nodes[i]);
        }
        Grid g = new Grid(map);
        ArrayBlockingQueue pathQueue = new ArrayBlockingQueue(100000);

        Ant a = new Ant(g, pathQueue, 2) {
            @Override
            protected void producePheromone() {
                Edge edge = g.getEdgeKeySet().iterator().next();
                EdgeInfo edgeInfo = g.getOrCreateEdgeInfo(edge);
                edgeInfo.getPheromone().add(5f);
                //System.out.print(edge.toString() + " " + edgeInfo.getDistance());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected Integer chooseNextNode() {
                Map<Edge, EdgeInfo> map = getPossibleNextEdgeInfoMap();
                System.out.println(Arrays.toString(map.keySet().toArray()));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Iterator<Edge> iter = map.keySet().iterator();

                return iter.next().getAsArray()[0];
            }
        };

        Thread t = new Thread(a);
        t.start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(200);
                    List<Ant> ants = new ArrayList<>();
                    ants.add(a);
                    //g.removeNode(1,ants);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        while(t.isAlive()) {
            List<Integer> path = (List<Integer>) pathQueue.poll();
            if(path != null) {
                System.out.println(Arrays.toString(path.toArray()));
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }
}