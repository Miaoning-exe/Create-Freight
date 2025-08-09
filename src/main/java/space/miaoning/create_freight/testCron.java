package space.miaoning.create_freight;

import com.cronutils.descriptor.CronDescriptor;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.parser.CronParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.util.Locale;

public class testCron {

    public static final Logger LOGGER = LogManager.getLogger();

    public static void testCronUtils() {
        try {
            // 直接为 QUARTZ 类型获取一个已定义好的实例
            CronDefinition cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ);

            // 创建一个解析器
            CronParser parser = new CronParser(cronDefinition);

            // 获取一个用于生成描述的实例，这里我们用中文
            CronDescriptor descriptor = CronDescriptor.instance(Locale.CHINESE);

            // 解析表达式并生成描述
            String description = descriptor.describe(parser.parse("*/45 * * * * ?"));

            // 如果代码能运行到这里并且没有出错，就在日志里打印成功信息
            LOGGER.info("========================================");
            LOGGER.info("Cron-utils 库验证成功！");
            LOGGER.info("Cron 表达式 '*/45 * * * * ?' 的中文描述是: " + description);
            LOGGER.info("========================================");

        } catch (Exception e) {
            // 如果出现任何错误，就打印失败信息
            LOGGER.error("Cron-utils 库验证失败！", e);
        }
    }
}
