package cn.mulanbay.ai.nlp.processor;

import cn.mulanbay.business.handler.BaseHandler;
import com.hankcs.hanlp.seg.common.Term;
import me.xiaosheng.chnlp.AHANLP;
import me.xiaosheng.chnlp.seg.POSFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 自然语言处理
 *
 * @see {https://github.com/jsksxs360/AHANLP}
 */
@Component
public class NLPProcessor extends BaseHandler {

    private static final Logger logger = LoggerFactory.getLogger(NLPProcessor.class);

    @Value("${mulanbay.nlp.model.init:false}")
    boolean modelInit;

    public NLPProcessor() {
        super("AHA自然语言处理");
    }

    @Override
    public void init() {
        logger.info("word2Vector模型是否需要初始化:{}",modelInit);
        if (modelInit) {
            initWord2VectorModel();
        }
    }

    @Override
    public void reload() {
        this.initWord2VectorModel();
    }

    /**
     * 初始化Word2Vector训练后的模型文件，加载需要一点时间，所有可以预加载
     */
    private void initWord2VectorModel() {
        Thread thread = new Thread(() -> {
            logger.info("开始初始化 Word2Vector model...");
            try {
                //第一次会自动初始化
                float f = sentenceSimilarity("test", "test");
                logger.info("initWord2VectorModel 测试匹配度:{}",f);
            } catch (Exception e) {
                logger.error("initWord2VectorModel error",e);
            }
            logger.info("初始化 Word2Vector model成功");
        });
        thread.start();
        logger.info("启动初始化 Word2Vector线程");
    }

    /**
     * NLP分词
     * NLP分词 (NLPSegment) 封装了感知机模型，由结构化感知机序列标注框架支撑，会同时执行词性标注和命名实体识别，准确率更高，适合生产环境使用。
     *
     * @param content
     * @return
     */
    public List<Term> nlpSegment(String content) {
        List<Term> nlpSegResult = AHANLP.NLPSegment(content);
        logger.debug("NLP分词结果：" + nlpSegResult.toString());
        return nlpSegResult;
    }

    /**
     * 通过NLP分词获得名称列表
     * @param content
     * @return
     */
    public List<String> nlpSegmentN(String content) {
        List<String> res = new ArrayList<>();
        List<Term> nlpSegResult = this.nlpSegment(content);
        // 过滤标点
        POSFilter.removePunc(nlpSegResult);
        // 保留名词
        POSFilter.selectPOS(nlpSegResult, Arrays.asList("n", "ns", "nr", "nt", "nz"));
        for(Term t : nlpSegResult){
            res.add(t.word);
        }
        return res;
    }

    /**
     * TextRank 摘取关键词
     * extractKeyword 函数通过第二个参数设定返回的关键词个数。内部通过 TextRank 算法计算每个词语的 Rank 值，并按 Rank 值降序排列，提取出前面的几个作为关键词
     *
     * @param content
     * @return
     */
    public List<String> extractKeyword(String content,Integer num) {
        if(num==null){
            num = 5;
        }
        List<String> nlpSegResult = AHANLP.extractKeyword(content,num);
        //logger.debug("NLP分词结果：" + nlpSegResult.toString());
        return nlpSegResult;
    }

    /**
     * 匹配度（句子的相似度）
     *
     * @param s1
     * @param s2
     * @return
     */
    public float sentenceSimilarity(String s1, String s2) {
        float v = AHANLP.sentenceSimilarity(s1, s2);
        logger.debug("[" + s1 + "] 和 [" + s2 + "] 的匹配度:" + v);
        return v;
    }

    /**
     * 获取平均相似度（句子的相似度）
     *
     * @param list
     * @return
     */
    public float avgSentenceSimilarity(List<String> list) {
        int n = list.size();
        if (n < 2) {
            return 0;
        } else {
            List<Float> fs = new ArrayList<>();
            String cu = list.get(0);
            for (int i = 1; i < n; i++) {
                String foods = list.get(i);
                float ss = this.sentenceSimilarity(cu, foods);
                fs.add(ss);
                //往下隔一个匹配
                int n2 = i + 2;
                if (n2 >= n) {
                    n2 = n2 - n;
                }
                float ss2 = this.sentenceSimilarity(foods, list.get(n2));
                fs.add(ss2);
                cu = foods;
            }
            return average(fs);
        }
    }

    /**
     * 计算平均值
     *
     * @param vs
     * @return
     */
    public float average(List<Float> vs) {
        float max = vs.get(0);
        float min = vs.get(0);
        int i = 0;
        float sum = 0;
        int n = vs.size();
        for (i = 0; i < n; i++) {
            float v = vs.get(i);
            if (v > max) {
                max = v;
            }
            if (v < min) {
                min = v;
            }
            sum += v;
        }
        if (n <= 3) {
            return sum / n;
        }
        return (sum - min - max) / (n - 2);
    }

}
