import java.util.*;

class VideoData {
    String videoId;
    String content;

    VideoData(String id,String content){
        this.videoId=id;
        this.content=content;
    }
}

class LRUCache<K,V> extends LinkedHashMap<K,V>{

    int capacity;

    LRUCache(int capacity){
        super(capacity,0.75f,true);
        this.capacity=capacity;
    }

    protected boolean removeEldestEntry(Map.Entry<K,V> eldest){
        return size()>capacity;
    }
}

class MultiLevelCache {

    LRUCache<String,VideoData> L1;
    LRUCache<String,VideoData> L2;
    HashMap<String,VideoData> L3;

    HashMap<String,Integer> accessCount;

    int L1Hits=0;
    int L2Hits=0;
    int L3Hits=0;

    int totalRequests=0;

    MultiLevelCache(){

        L1=new LRUCache<>(10000);
        L2=new LRUCache<>(100000);
        L3=new HashMap<>();

        accessCount=new HashMap<>();
    }

    public void addVideo(VideoData v){
        L3.put(v.videoId,v);
    }

    public VideoData getVideo(String videoId){

        totalRequests++;

        if(L1.containsKey(videoId)){
            L1Hits++;
            accessCount.put(videoId,accessCount.getOrDefault(videoId,0)+1);
            return L1.get(videoId);
        }

        if(L2.containsKey(videoId)){

            L2Hits++;

            VideoData v=L2.get(videoId);

            accessCount.put(videoId,accessCount.getOrDefault(videoId,0)+1);

            if(accessCount.get(videoId)>3){
                L1.put(videoId,v);
            }

            return v;
        }

        if(L3.containsKey(videoId)){

            L3Hits++;

            VideoData v=L3.get(videoId);

            L2.put(videoId,v);

            accessCount.put(videoId,1);

            return v;
        }

        return null;
    }

    public void invalidate(String videoId){

        L1.remove(videoId);
        L2.remove(videoId);
        L3.remove(videoId);
        accessCount.remove(videoId);
    }

    public void getStatistics(){

        double l1Rate=(L1Hits*100.0)/totalRequests;
        double l2Rate=(L2Hits*100.0)/totalRequests;
        double l3Rate=(L3Hits*100.0)/totalRequests;

        System.out.println("\nCache Statistics:");
        System.out.println("L1 Hit Rate: "+String.format("%.2f",l1Rate)+"%");
        System.out.println("L2 Hit Rate: "+String.format("%.2f",l2Rate)+"%");
        System.out.println("L3 Hit Rate: "+String.format("%.2f",l3Rate)+"%");
    }
}

public class VideoStreamingCache {

    public static void main(String[] args){

        MultiLevelCache cache=new MultiLevelCache();

        cache.addVideo(new VideoData("video_123","Movie A"));
        cache.addVideo(new VideoData("video_999","Movie B"));
        cache.addVideo(new VideoData("video_456","Movie C"));

        System.out.println(cache.getVideo("video_123").content);
        System.out.println(cache.getVideo("video_123").content);
        System.out.println(cache.getVideo("video_123").content);

        System.out.println(cache.getVideo("video_999").content);

        cache.getStatistics();
    }
}