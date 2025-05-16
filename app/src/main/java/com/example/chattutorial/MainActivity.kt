package com.example.chattutorial


import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.chattutorial.ui.theme.ChatTutorialTheme

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.client.query.CreateChannelParams
import io.getstream.chat.android.compose.ui.channels.ChannelsScreen
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.models.Channel
import io.getstream.chat.android.models.InitializationState
import io.getstream.chat.android.models.MemberData
import io.getstream.chat.android.models.User
import io.getstream.chat.android.offline.plugin.factory.StreamOfflinePluginFactory
import io.getstream.chat.android.state.plugin.config.StatePluginConfig
import io.getstream.chat.android.state.plugin.factory.StreamStatePluginFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1 - Set up the OfflinePlugin for offline storage
        val offlinePluginFactory = StreamOfflinePluginFactory(appContext = applicationContext)
        val statePluginFactory =
            StreamStatePluginFactory(config = StatePluginConfig(), appContext = this)

        // 2 - Set up the client for API calls and with the plugin for offline storage
        val client = ChatClient.Builder("gcrwt6e6pdww", applicationContext)
            .withPlugins(offlinePluginFactory, statePluginFactory)
            .logLevel(ChatLogLevel.ALL) // Set to NOTHING in prod
            .build()


        // 3 - Authenticate and connect the user
        val user = User(
            id = "test-hack-user-1",
            name = "Tutorial Droid",
            image = "https://bit.ly/2TIt8NR"
        )


        client.connectUser(
            user = user,
            token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoidGVzdC1oYWNrLXVzZXItMSJ9.xl8nEScGvy_hgvXoZfuQ9902gVvdEY9YV2FqZ6TxZzw"
        ).enqueue() {
            if (it.isSuccess) {
                Toast.makeText(this, "User connected", Toast.LENGTH_SHORT).show()

                createChannel(client)
            } else {
                Toast.makeText(this, "User error ${it.errorOrNull()}", Toast.LENGTH_SHORT).show()
                println("dsds error ${it.errorOrNull()}")

            }

        }


        setContent {
            // Observe the client connection state
            val clientInitialisationState by client.clientState.initializationState.collectAsState()

            ChatTheme {
                when (clientInitialisationState) {
                    InitializationState.COMPLETE -> {
                        ChannelsScreen(
                            title = stringResource(id = R.string.app_name),
                            isShowingHeader = true,
                            onChannelClick = { channel ->


                                startActivity(ChannelActivity.getIntent(this, channel.cid))


                            },
                            onBackPressed = { finish() }
                        )
                    }

                    InitializationState.INITIALIZING -> {
                        Text(text = "Initialising...")
                    }

                    InitializationState.NOT_INITIALIZED -> {
                        Text(text = "Not initialized...")
                    }
                }
            }
        }

    }

    fun createChannel(client: ChatClient) {

        client.channel(
            channelType = "messaging",
            channelId = "general"
        ).create(
            params = CreateChannelParams(

                members = listOf(
                    MemberData("test-hack-user-1"),
                       MemberData( "test-hack-user-2")

                ),


                extraData = mapOf(
                    "name" to "General",
                    "image" to "https://bit.ly/2TIt8NR"
                )

            )
        ).enqueue() {
            if (it.isSuccess) {
                Toast.makeText(this, "Channel created", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Channel error ${it.errorOrNull()}", Toast.LENGTH_SHORT).show()
                println("dsds error ${it.errorOrNull()}")

            }
        }
    }
}

@Composable
fun MessageScreen(channel: Channel, modifier: Modifier = Modifier) {


    Text(
        text = "Channel: ${channel.id}",
        modifier = modifier
    )
    
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ChatTutorialTheme {
        Greeting("Android")
    }
}