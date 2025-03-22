package studiplayer.ui;

import java.io.File;
import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import studiplayer.audio.AudioFile;
import studiplayer.audio.NotPlayableException;
import studiplayer.audio.PlayList;
import studiplayer.audio.SortCriterion;

public class Player extends Application {

	private boolean useCertPlayList = false;
	private PlayList playList;
	public static final String DEFAULT_PLAYLIST = "playlists/DefaultPlayList.m3u";
	private static final String PLAYLIST_DIRECTORY = "C:\\Users\\Mazen\\Desktop\\playlists";
	private static final String INITIAL_PLAY_TIME_LABEL = "00:00";
	private static final String NO_CURRENT_SONG = "-";
	private Button playButton, pauseButton, stopButton, nextButton, filterButton;
	private Label playListLabel, playTimeLabel, currentSongLabel;
	private ChoiceBox<SortCriterion> sortChoiceBox;
	private TextField searchTextField;
	private PlayerThread playThread;
	private TimerThread timeThread;
	private String totalTime, playlistName;
	private SongTable songTable;
	private AudioFile songToPlay;

	public Player() {
	}

	@Override
	public void start(Stage stage) throws Exception {
		// Load playlist
		if (!useCertPlayList) {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Open File");
			fileChooser.setInitialDirectory(new File(PLAYLIST_DIRECTORY));

			File file = fileChooser.showOpenDialog(stage);
			if (file == null) {
				playlistName = "";
			} else {
				playlistName = file.getName();
			}

			try {
				loadPlayList(file != null ? file.getPath() : DEFAULT_PLAYLIST);
			} catch (Exception e) {
				e.printStackTrace();
				loadPlayList(DEFAULT_PLAYLIST);
			}
		} else {
			loadPlayList(DEFAULT_PLAYLIST);
		}
		// Buttons and Components

		// Initialize UI components
		searchTextField = new TextField();
		sortChoiceBox = new ChoiceBox<>();
		filterButton = new Button("Display");

		playListLabel = new Label(String.format("%s\\%s", PLAYLIST_DIRECTORY, playlistName));
		playTimeLabel = new Label(INITIAL_PLAY_TIME_LABEL);
		currentSongLabel = new Label(NO_CURRENT_SONG);

		// Initialize buttons with icons
		playButton = createButton("play.jpg");
		pauseButton = createButton("pause.jpg");
		stopButton = createButton("stop.jpg");
		nextButton = createButton("next.jpg");

		// Set button actions
		playButton.setOnAction(event -> playCurrentSong());
		pauseButton.setOnAction(event -> pauseCurrentSong());
		stopButton.setOnAction(event -> stopCurrentSong());
		nextButton.setOnAction(event -> skipCurrentSong());

		// Initialize
		songTable = new SongTable(playList);
		totalTime = INITIAL_PLAY_TIME_LABEL;
		setButtonStates(false, true, true, false);
		// row selection
		songTable.setRowSelectionHandler(event -> handleSongSelection());

		// Initialize the default song if it is empty
		if (getSongToPlay() == null) {
			if (playList.size() > 0) {
				setSongToPlay(playList.getList().get(0));
				playList.jumpToAudioFile(songToPlay);
			}
		}
		// Stage title
		stage.setTitle("APA Player");

		// Filter pane
		TitledPane filterPane = new TitledPane("Filter", null);

		VBox filterBox = new VBox(10); // Vertical box with spacing of 10
		filterBox.setPadding(new Insets(5));

		HBox searchBox = new HBox(10); // Horizontal box for search label and text field
		searchBox.getChildren().addAll(new Label("Search text"), searchTextField);

		HBox sortBox = new HBox(10); // Horizontal box for sort label and choice box
		sortBox.getChildren().addAll(new Label("Sort by"), sortChoiceBox, filterButton);

		searchTextField.setPrefWidth(100);
		sortChoiceBox.setPrefWidth(100);

		filterBox.getChildren().addAll(searchBox, sortBox);
		filterPane.setContent(filterBox);

		SortCriterion[] criteria = SortCriterion.values();
		int index = 0;
		while (index < criteria.length) {
			sortChoiceBox.getItems().add(criteria[index]);
			index++;
		}
		filterButton.setOnAction(event -> {
			playList.setSearch(searchTextField.getText().trim());

			SortCriterion selectedCriterion = sortChoiceBox.getSelectionModel().getSelectedItem();
			playList.setSortCriterion(selectedCriterion != null ? selectedCriterion : SortCriterion.DEFAULT);
			songTable.refreshSongs();
		});

		// Details GridPane setup
		GridPane detailsGridPane = new GridPane();
		detailsGridPane.setPadding(new Insets(10)); // Increased padding for spacing
		detailsGridPane.setVgap(6); // Adjusted vertical spacing
		detailsGridPane.setHgap(12); // Adjusted horizontal spacing

		// Add elements to detailsGridPane
		detailsGridPane.addRow(0, new Label("Playlist"), playListLabel);
		detailsGridPane.addRow(1, new Label("Current Song"), currentSongLabel);
		detailsGridPane.addRow(2, new Label("Playtime"), playTimeLabel);

		// Song control buttons layout
		HBox songControls = new HBox(20); // Added spacing between buttons
		songControls.setAlignment(Pos.CENTER); // Center alignment
		songControls.getChildren().addAll(playButton, pauseButton, stopButton, nextButton);

		// Combine details and song controls in a vertical layout
		VBox bottomSection = new VBox(15, detailsGridPane, songControls); // Added spacing between sections
		bottomSection.setPadding(new Insets(10)); // Added padding for the bottom section

		// Main layout using BorderPane
		BorderPane mainPane = new BorderPane();
		mainPane.setTop(filterPane); // Filter pane at the top
		mainPane.setCenter(songTable); // Song table at the center
		mainPane.setBottom(bottomSection); // Bottom section at the bottom

		// Handle stage close request
		stage.setOnCloseRequest(event -> terminateThreads(false));

		// Scene setup
		Scene scene = new Scene(mainPane, 800, 500); // Set scene dimensions
		stage.setScene(scene);
		stage.setTitle("APA Player");
		stage.show();

	}

	// Handle song selection from table
	private void handleSongSelection() {
		Song song = songTable.getSelectionModel().getSelectedItem();
		if (song == null)
			return;

		totalTime = song.getLaenge();
		playList.jumpToAudioFile(song.getAudioFile());
		setSongToPlay(song.getAudioFile());

		if (timeThread != null) {
			if (playThread != null) {
				terminateThreads(false);
				startThreads(false);
			}
		}
		startThreads(false);
		songTable.selectSong(song.getAudioFile());
		setButtonStates(true, false, false, false);

	}

	public String getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(String totalTime) {
		this.totalTime = totalTime;
	}

	private void skipCurrentSong() {
		terminateThreads(false); // Stop current playback and timer
		playList.nextSong(); // Move to the next song
		AudioFile nextSong = playList.currentAudioFile();

		// Use ternary operator for the condition
		boolean hasNextSong = (nextSong != null);
		playList.jumpToAudioFile(hasNextSong ? nextSong : playList.currentAudioFile());
		songTable.selectSong(hasNextSong ? nextSong : playList.currentAudioFile());

		if (hasNextSong) {
			setSongToPlay(nextSong);
			playTimeLabel.setText(INITIAL_PLAY_TIME_LABEL); // Reset timer label
			startThreads(false); // Start threads for the new song
		} else {
			System.out.println("Last song");
		}

		setButtonStates(true, false, false, false);
		System.out.println("Song is skipped.");
	}

	private void stopCurrentSong() {
		terminateThreads(false);
		updateSongInfo(playList.currentAudioFile());
		setButtonStates(false, true, true, false);

	}

	private void pauseCurrentSong() {
		if (playThread != null) {
			getSongToPlay().togglePause();
			if (timeThread == null) {
				startThreads(true);
			} else {
				terminateThreads(true);
			}
			setButtonStates(true, false, false, false);

		}
	}

	private void playCurrentSong() {
		if (playThread != null && timeThread == null) {
			if (playThread != null) {
				if (timeThread == null) {
					// unpause
					getSongToPlay().togglePause();
					startThreads(true);
					setButtonStates(true, false, false, false);
				}
			}
		} else {
			terminateThreads(false);
			// start playing
			songTable.selectSong(playList.currentAudioFile());
			setSongToPlay(playList.currentAudioFile());
			startThreads(false);
			setButtonStates(true, false, false, false);

		}
	}

	private void updateSongInfo(AudioFile audiofile) {
		Platform.runLater(() -> {
			String currentPlayTime = playTimeLabel.getText();

			if (audiofile == null && timeThread != null) {
				timeThread.convertTime(currentPlayTime);
				playTimeLabel.setText(timeThread.getTime());
			} else {
				boolean isDifferentSong = !playList.currentAudioFile().toString().equals(currentSongLabel.getText());
				boolean noActiveThreads = (timeThread == null && playThread == null);

				if (isDifferentSong || noActiveThreads) {
					playTimeLabel.setText(INITIAL_PLAY_TIME_LABEL);
				} else {
					timeThread.convertTime(currentPlayTime);
					playTimeLabel.setText(timeThread.getTime());
				}
			}

			if (audiofile != null) {
				currentSongLabel.setText(audiofile.toString());
			}
		});

	}

	private Button createButton(String iconfile) {
		Button button = null;
		try {
			URL url = getClass().getResource("/icons/" + iconfile);
			if (url != null) {
				Image icon = new Image(url.toString());
				ImageView imageView = new ImageView(icon);

				// Configure ImageView
				imageView.setFitHeight(15);
				imageView.setFitWidth(15);

				// Create button with the icon
				button = new Button("", imageView);
				button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
				button.setStyle("-fx-background-color: #fff;");
			} else {
				throw new IllegalArgumentException("Icon file not found: " + iconfile);
			}
		} catch (Exception e) {
			System.out.println("Icon Error");
			System.exit(-1);
		}
		return button;
	}

	private void setButtonStates(boolean playButtonState, boolean pauseButtonState, boolean stopButtonState,
			boolean nextButtonState) {
		playButton.setDisable(playButtonState);
		pauseButton.setDisable(pauseButtonState);
		stopButton.setDisable(stopButtonState);
		nextButton.setDisable(nextButtonState);
	}

	public void setUseCertPlayList(boolean useCertPlaylist) {
		this.useCertPlayList = useCertPlaylist;
	}

	public void loadPlayList(String pathname) {
		if (useCertPlayList) {
			playlistName = pathname;
		} else {
			playlistName = DEFAULT_PLAYLIST;
		}

		if (pathname == null || pathname.isEmpty()) {
			playList = new PlayList(DEFAULT_PLAYLIST);
		} else {
			playList = new PlayList(pathname);
		}

	}

	public AudioFile getSongToPlay() {
		return songToPlay;
	}

	public void setSongToPlay(AudioFile songToPlay) {
		this.songToPlay = songToPlay;
	}

	class PlayerThread extends Thread {
		private volatile boolean stopped;

		public PlayerThread() {
			this.stopped = false;

		}

		@Override
		public void run() {
			while (!stopped) {
				try {
					updateSongInfo(playList.currentAudioFile());
					getSongToPlay().play();

				} catch (NotPlayableException e) {
					stopped = true;
					e.printStackTrace();
				}

			}

			if (!stopped) {
				playList.nextSong();

			}
		}

		public void terminate() {
			stopped = true;
			getSongToPlay().stop();
			interrupt();
		}
	}

	class TimerThread extends Thread {
		private volatile boolean stopped;
		private String time;

		public TimerThread() {
			this.stopped = false;
		}

		@Override
		public void run() {
			while (!stopped) {
				updateSongInfo(null);
				wait(1);
			}
		}

		public void convertTime(String time) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

			// Parse the time string into a LocalTime object, prefixed with "00:"
			LocalTime parsedTime = LocalTime.parse("00:" + time, formatter);

			// Increment the parsed time by 1 second
			LocalTime updatedTime = parsedTime.plusSeconds(1);

			// Format the updated time back to a string
			String formattedTime = updatedTime.format(formatter);

			// Update the time, removing the "00:" prefix
			setTime(formattedTime.substring(3));
		}

		public void wait(int seconds) {
			try {
				Thread.sleep(seconds * 1000);
			} catch (InterruptedException e) {

				stopped = true;
			}
		}

		public String getTime() {
			return this.time;
		}

		public void setTime(String time) {
			this.time = time;
		}

		public void terminate() {
			stopped = true;
			interrupt();
		}
	}

	private void startThreads(boolean onlyTimer) {
		// Start TimerThread if it doesn't already exist
		timeThread = (timeThread == null) ? new TimerThread() : timeThread;
		if (timeThread != null) {
			timeThread.start();
		}

		// Start PlayerThread if not onlyTimer and doesn't already exist
		if (!onlyTimer) {
			playThread = (playThread == null) ? new PlayerThread() : playThread;
			if (playThread != null) {
				playThread.start();
			}
		}
	}

	private void terminateThreads(boolean onlyTimer) {
		// Terminate TimerThread if it exists
		if (timeThread != null) {
			timeThread.terminate();
			timeThread = null;
		}

		// Terminate PlayerThread if not onlyTimer and it exists
		if (!onlyTimer) {
			if (playThread != null) {
				playThread.terminate();
				playThread = null;
			}
		}
	}

	public static void main(String[] args) {
		launch();

	}

}
