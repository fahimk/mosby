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

package com.hannesdorfmann.mosby.sample.mail.base.presenter;

import com.hannesdorfmann.mosby.mvp.rx.lce.scheduler.AndroidSchedulerTransformer;
import com.hannesdorfmann.mosby.sample.mail.base.view.AuthMailView;
import com.hannesdorfmann.mosby.sample.mail.model.event.MailLabelChangedEvent;
import com.hannesdorfmann.mosby.sample.mail.model.event.MailStaredEvent;
import com.hannesdorfmann.mosby.sample.mail.model.event.MailUnstaredEvent;
import com.hannesdorfmann.mosby.sample.mail.model.mail.Mail;
import com.hannesdorfmann.mosby.sample.mail.model.mail.MailProvider;
import de.greenrobot.event.EventBus;
import javax.inject.Inject;
import rx.Subscriber;

/**
 * @author Hannes Dorfmann
 */
public class RxMailPresenter<V extends AuthMailView<M>, M> extends RxAuthPresenter<V, M> {

  @Inject public RxMailPresenter(MailProvider mailProvider, EventBus eventBus) {
    super(mailProvider, eventBus);
  }

  public void starMail(final Mail mail, final boolean star) {

    // optimistic propagation
    if (star) {
      eventBus.post(new MailStaredEvent(mail.getId()));
    } else {
      eventBus.post(new MailUnstaredEvent(mail.getId()));
    }

    mailProvider.starMail(mail.getId(), star)
        .compose(new AndroidSchedulerTransformer<Mail>())
        .subscribe(new Subscriber<Mail>() {
          @Override public void onCompleted() {
          }

          @Override public void onError(Throwable e) {
            // Oops, something went wrong, "undo"
            if (star) {
              eventBus.post(new MailUnstaredEvent(mail.getId()));
            } else {
              eventBus.post(new MailStaredEvent(mail.getId()));
            }

            if (isViewAttached()) {
              if (star) {
                getView().showStaringFailed(mail);
              } else {
                getView().showUnstaringFailed(mail);
              }
            }
          }

          @Override public void onNext(Mail mail) {
          }
        });

    // Note: that we don't cancel this operation in detachView().
    // We want to ensure that this operation finishes
  }

  public void onEventMainThread(MailStaredEvent event) {
    if (isViewAttached()) {
      getView().markMailAsStared(event.getMailId());
    }
  }

  public void onEventMainThread(MailUnstaredEvent event) {
    if (isViewAttached()) {
      getView().markMailAsUnstared(event.getMailId());
    }
  }

  public void onEventMainThread(MailLabelChangedEvent event) {
    if (isViewAttached()) {
      getView().changeLabel(event.getMail(), event.getLabel());
    }
  }
}
