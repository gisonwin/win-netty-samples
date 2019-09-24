package com.gison.win.constant;

/**
 * @author <a href="mailto:gisonwin@qq.com">GisonWin</a>
 * @date 2019/9/4 15:53
 */
public enum DATATYPE {
    TBOM_TREE("tbom_tree"), TBOM_PERFORM("tbom_perform"), TBOM_PROGRESS("tbom_progress"), TBOM_REPORT("tbom_report"), TBOM_FINISH("tbom_finish");
    private String type;

    DATATYPE(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
