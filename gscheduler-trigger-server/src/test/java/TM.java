/**
 * @Author seven
 * @Date 2021/5/14 23:44
 * @Description
 * @Version 1.0
 */
public class TM {
    public static void main(String[] args) {
        String s="serivce:8080/xxx";
        int index = s.indexOf("/");
        System.out.println(s.substring(index));
    }
}
