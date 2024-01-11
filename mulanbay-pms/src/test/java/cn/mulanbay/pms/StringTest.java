package cn.mulanbay.pms;

public class StringTest {

    public static void main(String[] args){
        String sql="select sss from wwww {query_para} asssssss {query_para}";
        sql=sql.replace("{query_para}","abc");
        System.out.println(sql);
    }
}
