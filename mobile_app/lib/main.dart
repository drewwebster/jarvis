import 'package:flutter/material.dart';

void main() => runApp(new JarvisApp());

const String _name = "Arun";

final ThemeData _appTheme = new ThemeData(
  brightness: Brightness.dark,
  primaryColor: const Color(0xFF2E3E4E),
  scaffoldBackgroundColor: const Color(0xFF1B2936),
  accentColor: const Color(0xFF18FFFF),
);

class JarvisApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return new MaterialApp(
      title: 'Jarvis',
      theme: _appTheme,
      home: new JarvisHomePage(title: 'Jarvis'),
    );
  }
}

class JarvisHomePage extends StatefulWidget {
  JarvisHomePage({Key key, this.title}) : super(key: key);

  final String title;

  @override
  _JarvisHomePageState createState() => new _JarvisHomePageState();
}

class _JarvisHomePageState extends State<JarvisHomePage>
    with TickerProviderStateMixin {
  final TextEditingController _commandTextController =
      new TextEditingController();

  final List<ConversationMessage> _messages = <ConversationMessage>[];

  void _handleSubmitted(String text) {
    _commandTextController.clear();
    ConversationMessage message = new ConversationMessage(
      text: text,
      animationController: new AnimationController(
        //new
        duration: new Duration(milliseconds: 300), //new
        vsync: this, //new
      ),
    );
    setState(() {
      _messages.insert(0, message);
    });
    message.animationController.forward();
  }

  Widget _buildTextComposer() {
    return new IconTheme(
      data: new IconThemeData(color: Theme.of(context).primaryColor),
      child: new Container(
        margin: const EdgeInsets.symmetric(horizontal: 8.0),
        child: new Column(
          children: <Widget>[
            new Row(
              children: <Widget>[
                new Flexible(
                  child: new TextField(
                    textAlign: TextAlign.left,
                    controller: _commandTextController,
                    onSubmitted: _handleSubmitted,
                    decoration: new InputDecoration.collapsed(
                      hintText: "Send a message",
                    ),
                  ),
                ),
                new Container(
                  margin: new EdgeInsets.symmetric(horizontal: 4.0),
                  child: new IconButton(
                    icon: new Icon(Icons.send),
                    onPressed: () =>
                        _handleSubmitted(_commandTextController.text),
                  ),
                ),
                new Container(
                  margin: const EdgeInsets.symmetric(horizontal: 8.0),
                  child: new FloatingActionButton(
                    onPressed: null,
                    child: new Icon(Icons.mic),
                    mini: true,
                  ),
                ),
              ],
            )
          ],
        ),
      ),
    );
  }

  @override
  void dispose() {
    for (ConversationMessage message in _messages)
      message.animationController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return new Scaffold(
      appBar: new AppBar(
        title: new Text(widget.title),
      ),
      body: new Column(
        children: <Widget>[
          new Flexible(
            child: new ListView.builder(
              padding: new EdgeInsets.all(8.0),
              reverse: true,
              itemBuilder: (_, int index) => _messages[index],
              itemCount: _messages.length,
            ),
          ),
          new Divider(height: 1.0),
          new Container(
            child: _buildTextComposer(),
          ),
        ],
      ),
    );
  }
}

class ConversationMessage extends StatelessWidget {
  ConversationMessage({this.text, this.animationController});

  final String text;

  final AnimationController animationController;

  @override
  Widget build(BuildContext context) {
    return new SizeTransition(
      sizeFactor: new CurvedAnimation(
        parent: animationController,
        curve: Curves.easeOut,
      ),
      child: new Container(
        margin: const EdgeInsets.symmetric(vertical: 10.0),
        child: new Row(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: <Widget>[
            new Container(
              margin: const EdgeInsets.only(right: 16.0),
              child: new CircleAvatar(child: new Text(_name[0])),
            ),
            new Expanded(
              child: new Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: <Widget>[
                  new Text(_name, style: Theme.of(context).textTheme.subhead),
                  new Container(
                    margin: const EdgeInsets.only(top: 5.0),
                    child: new Text(text),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}
