package me.hopedev.vouchy.utils;

import me.hopedev.topggwebhooks.WebhookEvent;
import me.hopedev.topggwebhooks.WebhookListener;

public class WebhookHandler implements WebhookListener {

    @Override
    public void onWebhookRequest(WebhookEvent event) {
    if (event.isAuthorized()) {

        System.out.println("Vote received!");

    }
    }
}
