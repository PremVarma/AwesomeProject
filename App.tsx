import React, {useEffect} from 'react';
import {
  SafeAreaView,
  Text,
  NativeEventEmitter,
  NativeModules,
  StyleSheet,
  Alert,
} from 'react-native';
import {check, request, PERMISSIONS, RESULTS} from 'react-native-permissions';

const {ScreenshotModule} = NativeModules;
const screenshotEvents = new NativeEventEmitter(ScreenshotModule);

const App = () => {
  useEffect(() => {
    requestStoragePermission();

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

  const requestStoragePermission = async () => {
    const result = await check(PERMISSIONS.ANDROID.READ_EXTERNAL_STORAGE);

    if (result === RESULTS.GRANTED) {
      Alert.alert(
        'Permission granted',
        'You already have permission to read external storage.',
      );
    } else if (result === RESULTS.DENIED) {
      const requestResult = await request(
        PERMISSIONS.ANDROID.READ_EXTERNAL_STORAGE,
      );
      if (requestResult === RESULTS.GRANTED) {
        Alert.alert(
          'Permission granted',
          'Permission to read external storage has been granted.',
        );
      } else {
        Alert.alert('Permission denied', 'You cannot access external storage.');
      }
    } else if (result === RESULTS.BLOCKED) {
      Alert.alert(
        'Permission blocked',
        'You need to enable permission from settings.',
      );
    }
  };

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
