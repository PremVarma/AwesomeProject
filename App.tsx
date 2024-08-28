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
    const subscription = screenshotEvents.addListener(
      'screenshotDetected',
      eventData => {
        console.log('Screenshot detected!', eventData);
        Alert.alert(
          'Event Received!',
          `Title: ${
            eventData.title
          }, Timestamp: ${eventData.timestamp.toString()}`,
        );

        const {title, timestamp} = eventData;
        if (title && timestamp) {
          makeApiCall(timestamp, title);
        } else {
          Alert.alert('Event Data Missing', 'Title or Timestamp is missing.');
        }

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
      <Text style={styles.text}>Sublime App</Text>
    </SafeAreaView>
  );
};

const getMinutesFromTimestamp = (timestamp: number) => {
  const date = new Date(timestamp);
  const minutes = date.getMinutes().toString().padStart(2, '0');
  return minutes;
};

const makeApiCall = async (timestamp: number, title: string) => {
  try {
    const response = await fetch('http://rev.vet:40000/search', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        timestamp: getMinutesFromTimestamp(timestamp),
        title: title,
      }),
    });
    console.log(response, 'response');
    if (response.ok) {
      const responseData = await response.json();
      console.log('API response:', responseData);
      Alert.alert('API Call Success', 'Data successfully sent to the API!');
    } else if (response.status === 404) {
      console.error('We cannot find this content:', response.statusText);
      Alert.alert('API Call Success with 404', 'Add this to library');
    } else {
      console.error('API call failed:', response.statusText);
      Alert.alert('API Call Failed', 'Failed to send data to the API.');
    }
  } catch (error) {
    console.error('Error calling API:', error);
    Alert.alert('API Call Error', 'An error occurred while calling the API.');
  }
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
