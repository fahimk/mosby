/*
 * Copyright 2015 Hannes Dorfmann.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hannesdorfmann.mosby.sample.mail.dagger;

import com.hannesdorfmann.mosby.mvp.rx.lce.scheduler.AndroidSchedulerTransformer;
import com.hannesdorfmann.mosby.mvp.rx.lce.scheduler.SchedulerTransformer;
import com.hannesdorfmann.mosby.sample.mail.model.account.AccountManager;
import com.hannesdorfmann.mosby.sample.mail.model.account.DefaultAccountManager;
import com.hannesdorfmann.mosby.sample.mail.model.mail.MailGenerator;
import com.hannesdorfmann.mosby.sample.mail.model.mail.MailProvider;
import com.hannesdorfmann.mosby.sample.mail.model.mail.RandomMailGenerator;
import dagger.Module;
import dagger.Provides;
import de.greenrobot.event.EventBus;
import javax.inject.Singleton;

/**
 * @author Hannes Dorfmann
 */
@Module public class MailModule {

  // Singletons
  private static MailGenerator generator = new RandomMailGenerator();
  private static AccountManager accountManager = new DefaultAccountManager();
  private static MailProvider mailProvider =
      new MailProvider(accountManager, generator);

  @Singleton @Provides public AccountManager providesAccountManager() {
    return accountManager;
  }

  @Singleton @Provides public SchedulerTransformer providesSchedulerTransformer() {
    return new AndroidSchedulerTransformer();
  }

  @Singleton @Provides public EventBus providesEventBus() {
    return EventBus.getDefault();
  }

  @Singleton @Provides public MailProvider providesMailProvider() {
    return mailProvider;
  }

  @Singleton @Provides public MailGenerator providesMailGenerator(){
    return generator;
  }
}
