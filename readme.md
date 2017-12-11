tlbot
-----

A simple chat translation bot for Typetalk. If you @mention it, it will translate
between English and Japanese for you. A normal @mention will translate your message,
whereas if you reply to someone else's message and @mention the bot it will translate
their message for you.

Typetalk向けの簡単な翻訳ボットです。ボットを＠メンションしたら、英語を和訳したり日本語を英訳します。普通の＠メンションで送信したメッセージが翻訳されますが、他のメッセージに返信してボットを＠メンションしたらそのメッセージが翻訳されます。


Running It
--
Should run out of the box except for one missing config file called "secrets.conf"
which holds API keys for Typetalk and GCP.
```$xslt
typetalk {
  token = "YOUR_TYPETALK_TOKEN"
}

google-translate {
  key = "YOUR_GCP_TOKEN"
}
```


Known Issues
--
Because google translate html encodes any special characters you send it, some things
may not come back exactly like they went in, such as `「` and `」` becoming `"`. 

Also, the quality of Google's ENG <-> JP translations often leaves much to be desired. 