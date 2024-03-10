package hello.springtx.apply;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@SpringBootTest
@Slf4j
public class InternalCallTestV2 {

    @Autowired CallService callService;
    @Autowired InternalService internalService;

    @Test
    void printProxy() {
        log.info("callService class={}", callService.getClass());
        log.info("internalService class={}", internalService.getClass());
    }

    @Test
    void externalCallV2() {
        callService.external();
    }

    @TestConfiguration
    static class InternalCallV1TestConfig {

        @Bean
        CallService callService() {
            return new CallService(internalService());
        }

        @Bean
        InternalService internalService() {
            return new InternalService();
        }
    }

    @Slf4j
    @RequiredArgsConstructor
    static class CallService {
        private final InternalService internalService;

        public void external() {
            log.info("call external");
            printTxInfo();
            //internal()을 다른 클래스의 외부호출로 변경
            internalService.internal();
        }

        private void printTxInfo() {
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("tx active={}", txActive);
//            boolean readonly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
//            log.info("tx readonly={}", readonly);
        }
    }

    @Slf4j
    static  class InternalService {

        @Transactional
        public void internal() {
            log.info("call internal");
            printTxInfo();
        }

        private void printTxInfo() {
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("tx active={}", txActive);
//            boolean readonly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
//            log.info("tx readonly={}", readonly);
        }
    }
}
