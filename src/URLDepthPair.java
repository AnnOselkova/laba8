import java.net.*;

public class URLDepthPair {
    private URL url; // хранит url  в виде объкта url
    private int d; // глубина для ссылки

    public URLDepthPair(String url, int depth) throws MalformedURLException{
        this.url = new URL(url); // создаем url на основе строки
        this.d = depth; // сохраняем глубину
    }

    public URL getUrl(){
        return url;
    }
    public int getDepth() {
        return d;
    }

    public String toString(){ // выводим объкт в консоль
        return url.toString() + " " + d;
    }

    @Override
    public boolean equals(Object o){ // для сравнения двух ссылок
        if(o instanceof URLDepthPair pr){
            return url.equals(pr.url);//сравнение объектов типа url
        }
        return false;
    }
    @Override
    public int hashCode(){
        return url.hashCode();
    }
}
