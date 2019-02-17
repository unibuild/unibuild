package net.unibld.client.desktop.controller;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import net.unibld.client.desktop.util.javafx.JavaFXController;

public class EditorController extends JavaFXController {
	private static final Pattern XML_TAG = Pattern.compile("(?<ELEMENT>(</?\\h*)(\\w+)([^<>]*)(\\h*/?>))"
    		+"|(?<COMMENT><!--[^<>]+-->)");
    
    private static final Pattern ATTRIBUTES = Pattern.compile("(\\w+\\h*)(=)(\\h*\"[^\"]+\")");
    
    private static final int GROUP_OPEN_BRACKET = 2;
    private static final int GROUP_ELEMENT_NAME = 3;
    private static final int GROUP_ATTRIBUTES_SECTION = 4;
    private static final int GROUP_CLOSE_BRACKET = 5;
    private static final int GROUP_ATTRIBUTE_NAME = 1;
    private static final int GROUP_EQUAL_SYMBOL = 2;
    private static final int GROUP_ATTRIBUTE_VALUE = 3;

   
    @FXML
    private GridPane editorGrid;

	private CodeArea codeArea;
    
	@FXML
	public void initialize() {
		codeArea = new CodeArea();
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));

        codeArea.textProperty().addListener((obs, oldText, newText) -> {
            codeArea.setStyleSpans(0, computeHighlighting(newText));
        });
        
        
		StackPane stackPane = new StackPane(new VirtualizedScrollPane<>(codeArea));
		//Scene scene = new Scene(stackPane, 600, 400);
        //scene.getStylesheets().add(XMLEditor.class.getResource("xml-highlighting.css").toExternalForm());
        //primaryStage.setScene(scene);
        //primaryStage.setTitle("XML Editor Demo");
        //primaryStage.show();
		
		//editorGrid.getScene().getStylesheets().add(getClass().getResource("/xml-highlighting.css").toExternalForm());
		editorGrid.getChildren().add(stackPane);
	}
	
	
	 private static StyleSpans<Collection<String>> computeHighlighting(String text) {
	    	
	        Matcher matcher = XML_TAG.matcher(text);
	        int lastKwEnd = 0;
	        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
	        while(matcher.find()) {
	        	
	        	spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
	        	if(matcher.group("COMMENT") != null) {
	        		spansBuilder.add(Collections.singleton("comment"), matcher.end() - matcher.start());
	        	}
	        	else {
	        		if(matcher.group("ELEMENT") != null) {
	        			String attributesText = matcher.group(GROUP_ATTRIBUTES_SECTION);
	        			
	        			spansBuilder.add(Collections.singleton("tagmark"), matcher.end(GROUP_OPEN_BRACKET) - matcher.start(GROUP_OPEN_BRACKET));
	        			spansBuilder.add(Collections.singleton("anytag"), matcher.end(GROUP_ELEMENT_NAME) - matcher.end(GROUP_OPEN_BRACKET));

	        			if(!attributesText.isEmpty()) {
	        				
	        				lastKwEnd = 0;
	        				
	        				Matcher amatcher = ATTRIBUTES.matcher(attributesText);
	        				while(amatcher.find()) {
	        					spansBuilder.add(Collections.emptyList(), amatcher.start() - lastKwEnd);
	        					spansBuilder.add(Collections.singleton("attribute"), amatcher.end(GROUP_ATTRIBUTE_NAME) - amatcher.start(GROUP_ATTRIBUTE_NAME));
	        					spansBuilder.add(Collections.singleton("tagmark"), amatcher.end(GROUP_EQUAL_SYMBOL) - amatcher.end(GROUP_ATTRIBUTE_NAME));
	        					spansBuilder.add(Collections.singleton("avalue"), amatcher.end(GROUP_ATTRIBUTE_VALUE) - amatcher.end(GROUP_EQUAL_SYMBOL));
	        					lastKwEnd = amatcher.end();
	        				}
	        				if(attributesText.length() > lastKwEnd)
	        					spansBuilder.add(Collections.emptyList(), attributesText.length() - lastKwEnd);
	        			}

	        			lastKwEnd = matcher.end(GROUP_ATTRIBUTES_SECTION);
	        			
	        			spansBuilder.add(Collections.singleton("tagmark"), matcher.end(GROUP_CLOSE_BRACKET) - lastKwEnd);
	        		}
	        	}
	            lastKwEnd = matcher.end();
	        }
	        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
	        return spansBuilder.create();
	    }
	 
	 public void setContents(String xml) {
		 codeArea.replaceText(0, 0, xml);

	 }
}
