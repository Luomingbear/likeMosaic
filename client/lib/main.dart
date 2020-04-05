import 'package:agora_flutter_quickstart/src/pages/CallPage.dart';
import 'package:agora_flutter_quickstart/src/pages/PairPage.dart';
import 'package:agora_flutter_quickstart/src/pages/call.dart';
import 'package:flutter/material.dart';
import './src/pages/index.dart';

void main() => runApp(MyApp());

class MyApp extends StatelessWidget {
  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: IndexPage(),
    );
  }
}
