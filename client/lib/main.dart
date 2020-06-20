<<<<<<< HEAD
import 'package:agora_flutter_quickstart/src/pages/call.dart';

import "src/pages/CallPage.dart";
import 'package:flutter/material.dart';
=======
import 'package:agora_flutter_quickstart/src/pages/CallPage.dart';
import 'package:agora_flutter_quickstart/src/pages/PairPage.dart';
import 'package:agora_flutter_quickstart/src/pages/call.dart';
import 'package:flutter/material.dart';
import './src/pages/index.dart';
>>>>>>> 5a1763a33554b801bd739114e751a6613121ca79

void main() => runApp(MyApp());

class MyApp extends StatelessWidget {
  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
<<<<<<< HEAD
        primarySwatch: Colors.purple,
      ),
      home: CallPage(
        channelName: "bearever",
      ),
=======
        primarySwatch: Colors.blue,
      ),
      home: IndexPage(),
>>>>>>> 5a1763a33554b801bd739114e751a6613121ca79
    );
  }
}
