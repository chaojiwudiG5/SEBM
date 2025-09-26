package group5.sebm.common.constant;

public class NotificationConstant {
        /**
         * 模板标题最大长度
         */
        public static final int MAX_TEMPLATE_TITLE_LENGTH = 100;

        /**
         * 模板内容最大长度
         */
        public static final int MAX_TEMPLATE_CONTENT_LENGTH = 1000;

        /**
         * 时间偏移量最小值
         */
        public static final int MIN_TIME_OFFSET = 0;

        /**
         * 时间偏移量最大值（7天的秒数：7*24*60*60）
         */
        public static final int MAX_TIME_OFFSET_SECONDS = 604800;

        /**
         * 最大时间偏移天数
         */
        public static final int MAX_TIME_OFFSET_DAYS = 7;
}
