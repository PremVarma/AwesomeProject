import React, {useEffect} from 'react';
import {
  SafeAreaView,
  Text,
  NativeEventEmitter,
  NativeModules,
  StyleSheet,
  Alert,
} from 'react-native';

const {ScreenshotModule} = NativeModules;
const screenshotEvents = new NativeEventEmitter(ScreenshotModule);

const App = () => {
  useEffect(() => {
    Alert.alert('App Initialized!');
    const subscription = screenshotEvents.addListener(
      'screenshotDetected',
      () => {
        console.log('Screenshot detected!');
        Alert.alert('Event Received!');
        // Handle screenshot event here
      },
    );

    const subscriptionInit = screenshotEvents.addListener(
      'mainActivityEvent',
      message => {
        Alert.alert('MainAcitivity Received!');
        console.log(message); // Logs the message emitted when MainActivity is created
      },
    );

    // Clean up the subscription on component unmount
    return () => {
      subscription.remove();
      subscriptionInit.remove();
    };
  }, []);

  return (
    <SafeAreaView style={styles.container}>
      <Text style={styles.text}>Screenshot Detection App</Text>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#fff',
  },
  text: {
    fontSize: 18,
    fontWeight: 'bold',
  },
});

export default App;
