package cn.mulanbay.pms.handler.bean.consume;

import java.io.Serializable;

/**
 * 消费记录匹配
 *
 * @author fenghong
 * @create 2018-01-20 21:44
 */
public class GoodsTypeCompareBean implements Serializable {

    private static final long serialVersionUID = 5066451654516299863L;

    private Long goodsTypeId;

    //参与比较的消费名称
    private String compareName;

    private String tags;

    public Long getGoodsTypeId() {
        return goodsTypeId;
    }

    public void setGoodsTypeId(Long goodsTypeId) {
        this.goodsTypeId = goodsTypeId;
    }

    public String getCompareName() {
        return compareName;
    }

    public void setCompareName(String compareName) {
        this.compareName = compareName;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}
