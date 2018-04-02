import 'package:flutter/material.dart';

void main() => runApp(new JarvisApp());

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
  _MyHomePageState createState() => new _MyHomePageState();
}

class _MyHomePageState extends State<JarvisHomePage> {
  @override
  Widget build(BuildContext context) {
    return new Scaffold(
      appBar: new AppBar(
        title: new Text(widget.title),
      ),
    );
  }
}
