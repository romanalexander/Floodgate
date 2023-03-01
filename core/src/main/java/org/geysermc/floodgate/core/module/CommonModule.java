/*
 * Copyright (c) 2019-2023 GeyserMC. http://geysermc.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * @author GeyserMC
 * @link https://github.com/GeyserMC/Floodgate
 */

package org.geysermc.floodgate.core.module;

import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import io.netty.util.AttributeKey;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.geysermc.event.Listener;
import org.geysermc.floodgate.api.player.FloodgatePlayer;
import org.geysermc.floodgate.core.event.EventBus;
import org.geysermc.floodgate.core.event.lifecycle.ShutdownEvent;
import org.geysermc.floodgate.core.util.Constants;
import org.geysermc.floodgate.crypto.AesCipher;
import org.geysermc.floodgate.crypto.AesKeyProducer;
import org.geysermc.floodgate.crypto.Base64Topping;
import org.geysermc.floodgate.crypto.FloodgateCipher;
import org.geysermc.floodgate.crypto.KeyProducer;

@Factory
@Listener
public class CommonModule {
    @Bean
    @Singleton
    @Named("commonPool")
    public ExecutorService commonPool() {
        return new ThreadPoolExecutor(0, 20, 60L, TimeUnit.SECONDS, new SynchronousQueue<>());
    }

    @Inject
    public void registerShutdown(
            EventBus eventBus,
            @Named("commonPool") ExecutorService commonPool,
            @Named("commonScheduledPool") ScheduledExecutorService commonScheduledPool
    ) {
        eventBus.subscribe(ShutdownEvent.class, ignored -> {
            commonPool.shutdown();
            commonScheduledPool.shutdown();
        });
    }

    @Bean
    @Singleton
    public KeyProducer keyProducer() {
        return new AesKeyProducer();
    }

    @Bean
    @Singleton
    public FloodgateCipher cipher() {
        return new AesCipher(new Base64Topping());
    }

    @Bean
    @Singleton
    @Named("gitBranch")
    public String gitBranch() {
        return Constants.GIT_BRANCH;
    }

    @Bean
    @Singleton
    @Named("buildNumber")
    public Integer buildNumber() {
        return Constants.BUILD_NUMBER;
    }

    @Bean
    @Singleton
    @Named("kickMessageAttribute")
    public AttributeKey<String> kickMessageAttribute() {
        return AttributeKey.valueOf("floodgate-kick-message");
    }

    @Bean
    @Singleton
    @Named("playerAttribute")
    public AttributeKey<FloodgatePlayer> playerAttribute() {
        return AttributeKey.valueOf("floodgate-player");
    }
}
