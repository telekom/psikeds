KB-META:
01: id psiKedsLetterEnvelope
02: name "psiKeds letter envelope reference knowledge base"
03: teaser "knowledge base for evaluating the implementation"
04: release "0.1.1"
05: (C) "2014, Karsten Reincke, Deutsche Telekom AG, Darmstadt"

/* 
licensing 
 "proprietary license: All rights are reserved. Feel free to contact:
  opensource@telekom.de"
*/

06: licensing
"CC-SA-BY 3.0 (https://creativecommons.org/licenses/by-sa/3.0/). 
You should have got a version of this license together with this file."

07: created 2014-04-04
08: modified 2014-04-16 
09: language "en_EN"
10: engineer "Karsten Reincke" "Marco Juliano"
11: description 
  "A reference knowledge base for testing the psiKeds inference machine"
  "Domain: Convey a correctly packed and 
   stamped German C4/C5/C56/C6 letter"
  
PSIKEDS-SENSORS:

sensor NamedColor (
  label "Color-Names"
  description "perceptible colors denoted by their names"
  perceives  {
    str namedColorAzure : "azure" 
    str namedColorPink : "pink" 
    str namedColorIvory : "ivory" 
    str namedColorWhite : "white" 
    }
)

sensor StampValue (
  label "Stamp-Value"
  unit "Cent"
  perceives { 
    int Euro045 : 45 
    int Euro060 : 60
    int Euro090 : 90
    int Euro145 : 145
    int Euro240 : 240
  }
)

sensor PreprintedAddrField (
  label "Preprinted-Address-Field"
  description "contains a preprinted address field"
  perceives {
    str withPreprintedAddrField : "T"
    str withoutPreprintedAddrField : "F"
  }
)

sensor PaperImportance (
  label "Paper-Thickness"
  description "thickness of paper as one criteria of paper quality"
  unit "g/qm"
  perceives {
    int P1Importance : 1 
    int P2Importance : 2 
    int P3Importance : 3 
  }
)

sensor DinANumber (
  label "Din-A-Numbers"
  description "sensor to perceive Din A Format Numbers"
  perceives {
    int DinA0Number : 0 
    int DinA1Number : 1 
    int DinA2Number : 2 
    int DinA3Number : 3 
    int DinA4Number : 4 
    int DinA5Number : 5 
    int DinA6Number : 6 
    int DinA7Number : 7 
    int DinA8Number : 8 
    int DinA9Number : 9 
    int DinA10Number : 10 
  }
)

sensor DinCNumber (
  label "Din-C-Numbers"
  description "sensor to perceive Din C Format Numbers"
  perceives {
    float DinC0Number : 0.0 
    float DinC1Number : 1.0 
    float DinC2Number : 2.0 
    float DinC3Number : 3.0 
    float DinC4Number : 4.0 
    float DinC5Number : 5.0 
    float DinC56Number : 5.6 
    float DinC6Number : 6.0 
    float DinC7Number : 7.0 
    float DinC8Number : 8.0 
    float DinC9Number : 9.0 
    float DinC10Number : 10.0 
  }
)

sensor DinFormatHeight (
  label "Din-Format-Height"
  description 
    "DIN form based heights of paper sizes:
     DIN-A as portrait (height > width )
     DIN-C as landscape (width > height)"
  unit "cm"
  perceives {
    float DinA0Height : 118.9 
    float DinA1Height : 84.1 
    float DinA2Height : 59.4 
    float DinA3Height : 42.0 
    float DinA4Height : 29.7 
    float DinA5Height : 21.0 
    float DinA6Height : 14.8 
    float DinA7Height : 10.5 
    float DinA8Height : 7.4 
    float DinA9Height : 5.2 
    float DinA10Height : 3.7 

    float DinC0Height : 91.7 
    float DinC1Height : 64.8 
    float DinC2Height : 45.8 
    float DinC3Height : 32.4 
    float DinC4Height : 22.9 
    float DinC5Height : 16.2 
    float DinC6Height : 11.4 
    float DinC7Height : 8.1 
    float DinC8Height : 5.7 
    float DinC9Height : 4.0 
    float DinC10Height : 2.8 
  }
)

sensor DinFormatWidth (
  label "Din-Format-Width"
  description 
    "DIN form based widths of paper sizes:
     DIN-A as portrait (height > width )
     DIN-C as landscape (width > height)"
  unit "cm"
  perceives {
    float DinA0Width : 84.1 
    float DinA1Width : 59.4 
    float DinA2Width : 42.0 
    float DinA3Width : 29.7 
    float DinA4Width : 21.0 
    float DinA5Width : 14.8 
    float DinA6Width : 10.5 
    float DinA7Width : 7.4 
    float DinA8Width : 5.2 
    float DinA9Width : 3.7 
    float DinA10Width : 2.6 

    float DinC0Width : 129.7 
    float DinC1Width : 91.7 
    float DinC2Width : 64.8 
    float DinC3Width : 45.8 
    float DinC4Width : 32.4 
    float DinC5Width : 22.9 
    float DinC6Width : 16.2 
    float DinC7Width : 11.4 
    float DinC8Width : 8.1 
    float DinC9Width : 5.7 
    float DinC10Width : 4.0 
  }
)

sensor Weight (
  label "weight"
  description "weight of an envelope or a paper page or both together"
  unit "g"
  perceives {
    float Weight1P1 : 1.1 
    float Weight1P3 : 1.3 
    float Weight1P5 : 1.5 
    float Weight1P7 : 1.7 
    float Weight2P2 : 2.2 
    float Weight3P2 : 3.2 
    float Weight4P2 : 4.2 
    float Weight4P4 : 4.4 
    float Weight6P5 : 6.5 
    float Weight8P8 : 8.8 
    float Weight13P0 : 13.2 
    float Weight17P5 : 17.5 
    
    float.range cardWeight : < min 1.0, max 2.0,  inc 0.1 >
    float.range letterWeight : < min 2.0,  max 31.0,  inc 0.1 >
  }
)

sensor CountSectors (
  label "count-sectors"
  description "counts the number of sections evoked by the folding method"
  perceives {
    int.range howManySections : < min 1, max 4, inc 1 >
   }
)

PSIKEDS-CONCEPTS:

concept DinA6Letter (
  label "DIN-A6-Values"
  description "DIN A6 constitutive values"
  denotes-entities-with-the-features {
      [ *int> DinA6Number *sensed-by> DinANumber ]
    & [ *float> DinA6Height *sensed-by> DinFormatHeight ]
    & [ *float> DinA6Width *sensed-by> DinFormatWidth ]
    & [ *float> Weight1P1 *sensed-by> Weight ]
  }
)

concept DinA5Letter (
  label "DIN-A5-Values"
  description "DIN A5 constitutive values"
  denotes-entities-with-the-features {
      [ *int> DinA5Number *sensed-by> DinANumber ]
    & [ *float> DinA5Height *sensed-by> DinFormatHeight ]
    & [ *float> DinA5Width *sensed-by> DinFormatWidth ]
    & [ *float> Weight2P2 *sensed-by> Weight ]
  }
)

concept DinA4Letter (
  label "DIN-A4-Values"
  description "DIN A4 constitutive values"
  denotes-entities-with-the-features {
      [ *int> DinA4Number *sensed-by> DinANumber ]
    & [ *float> DinA4Height *sensed-by> DinFormatHeight ]
    & [ *float> DinA4Width *sensed-by> DinFormatWidth ]
    & [ *float> Weight4P4 *sensed-by> Weight ]
  }
)

concept DinA3Letter (
  label "DIN-A3-Values"
  description "DIN A3 constitutive values"
  denotes-entities-with-the-features {
      [ *int> DinA3Number *sensed-by> DinANumber ]
    & [ *float> DinA3Height *sensed-by> DinFormatHeight ]
    & [ *float> DinA3Width *sensed-by> DinFormatWidth ]
    & [ *float> Weight8P8 *sensed-by> Weight ]
  }
)

concept DinA2Letter (
  label "DIN-A2-Values"
  description "DIN A2 constitutive values"
  denotes-entities-with-the-features {
      [ *int> DinA2Number *sensed-by> DinANumber ]
    & [ *float> DinA2Height *sensed-by> DinFormatHeight ]
    & [ *float> DinA2Width *sensed-by> DinFormatWidth ]
    & [ *float> Weight17P5 *sensed-by> Weight ]
  }
)

concept DinA6CardConveyable (
  label "Conveyable-DIN-A6-Writing-Card"
  description "constitutive values for a conveyable DIN A6 writing card"
  denotes-entities-with-the-features {
      [ *str> withPreprintedAddrField *sensed-by> PreprintedAddrField]
    & [ *int> DinA6Number *sensed-by> DinANumber ]
    & [ *float> DinA6Height *sensed-by> DinFormatHeight ]
    & [ *float> DinA6Width *sensed-by> DinFormatWidth ]  
  }
)

concept DinA6CardUnconveyable (
  label "Unconveyable-DIN-A6-Writing-Card"
  description "constitutive values for an unconveyable DIN A6 writing card"
  denotes-entities-with-the-features {
      [ *str> withoutPreprintedAddrField *sensed-by> PreprintedAddrField]
    & [ *int> DinA6Number *sensed-by> DinANumber ]
    & [ *float> DinA6Height *sensed-by> DinFormatHeight ]
    & [ *float> DinA6Width *sensed-by> DinFormatWidth  ]
  }
)

concept DinC4Envelope (
  label "DIN-C4-Envelope"
  description "DIN C4 constitutive values"
  denotes-entities-with-the-features {
      [ *float> DinC4Number *sensed-by> DinCNumber  ]
    & [ *float> DinC4Height *sensed-by> DinFormatHeight  ]
    & [ *float> DinC4Width *sensed-by> DinFormatWidth  ]
    & [ *float> Weight13P0 *sensed-by> Weight ]
  }
)

concept DinC5Envelope (
  label "DIN-C5-Envelope"
  description "DIN C5 constitutive values"
  denotes-entities-with-the-features {
      [ *float> DinC5Number *sensed-by> DinCNumber  ]
    & [ *float> DinC5Height *sensed-by> DinFormatHeight  ]
    & [ *float> DinC5Width *sensed-by> DinFormatWidth  ]
    & [ *float> Weight6P5 *sensed-by> Weight ]
  }
)

concept DinC56Envelope (
  label "DIN-C5,6-Envelope"
  description "DIN C5,6 constitutive values"
  denotes-entities-with-the-features {
      [ *float> DinC56Number *sensed-by> DinCNumber  ]
    & [ *float> DinC6Height *sensed-by> DinFormatHeight  ]
    & [ *float> DinC5Width *sensed-by> DinFormatWidth  ]
    & [ *float> Weight4P2 *sensed-by> Weight ]
  }
)

concept DinC6Envelope (
  label "DIN-C6-Envelope"
  description "DIN C6 constitutive values"
  denotes-entities-with-the-features {
      [ *float> DinC6Number *sensed-by> DinCNumber  ]
    & [ *float> DinC6Height *sensed-by> DinFormatHeight  ]
    & [ *float> DinC6Width *sensed-by> DinFormatWidth  ]
    & [ *float> Weight3P2 *sensed-by> Weight ]    
  }
)

concept DinA6Entity (
  label "DIN-A6-Definition"
  description "concept denotes any entity with A6 values"
  denotes-entities-with-the-features {
      [ *int> DinA6Number *sensed-by> DinANumber ]
    & [ *float> DinA6Height *sensed-by> DinFormatHeight ]
    & [ *float> DinA6Width *sensed-by> DinFormatWidth  ]
  }
)

PSIKEDS-PURPOSES:
purpose informRemoteAddressee (
  root true
  label "inform-remote-addressee"
  description "something to inform a remote addressees"
)
  
 
purpose fileMessages (
  root false
  label "write-messages-on"
  description "something to write a messages on it"
)

purpose compressMessageMedia (
  label "compress-message-media"
  description "something usable to compress the media containing the messages"
)

purpose protectMessage (
  label "protect-message-media"
  description "something usable to protect the media containing the messages"
)
  
purpose enableMessageTransport (
  label "enable-message-transport"
  description "something usable to enable the transport of a protected message"
)

purpose prepareMultipleLetterWritings (
  label "prepare-multiple-letter-writings"
  description "something usable to prepare the flexible writings of tomorrow"
)

PSIKEDS-VARIANTS:

variant CompressedLetter (
  label "Compressed-Letter"
  description "a letter containing a compressed media"
  type explicit
  specified.by NamedColor as {
      *str> namedColorAzure
    | *str> namedColorPink 
    | *str> namedColorIvory
    | *str> namedColorWhite
  }
  
  specified.by DinCNumber as {
      *float> DinC4Number 
    | *float> DinC5Number 
    | *float> DinC56Number
    | *float> DinC6Number      
  }
  
  perceived.by Weight within *floatRange> letterWeight
)

variant UncompressedLetter (
  label "Uncompressed-Letter"
  description "a letter containing an uncompressed media"
  type explicit
  
  specified.by NamedColor as {
      *str> namedColorAzure 
    | *str> namedColorPink
    | *str> namedColorIvory
    | *str> namedColorWhite
  }
  
  specified.by DinCNumber as {
     *float> DinC4Number 
    | *float> DinC5Number 
    | *float> DinC6Number      
  }
  
  perceived.by Weight within *floatRange> letterWeight
)

variant Postcard (
  label "Postcard"
  description "standard postcard"
  type explicit
  
  specified.by NamedColor as {
      *str> namedColorIvory 
    | *str> namedColorWhite
  }
  
  perceived.by Weight within *floatRange> cardWeight
)

variant Envelope (
  label "Envelope"
  description "an envelope for protecting a written message"
  type explicit
  
  specified.by NamedColor as {
      *str> namedColorAzure 
    | *str> namedColorPink
    | *str> namedColorIvory 
    | *str> namedColorWhite
  }
  
  classified.as { 
    *concept> DinC4Envelope 
    | *concept> DinC5Envelope
     | *concept> DinC56Envelope
    | *concept> DinC6Envelope
  }
)

variant WritingPaper (
  label "WritingPaper"
  description "a page of letter paper"
  type explicit
  
  specified.by NamedColor as {
      *str> namedColorAzure
    | *str> namedColorPink
    | *str> namedColorIvory
    | *str> namedColorWhite
  }
  
  specified.by PaperImportance as {
      *int> P1Importance
    | *int> P2Importance
    | *int> P3Importance
  }
  
  perceived.by CountSectors within *intRange> howManySections
  
  classified.as {
     *concept> DinA6Letter
     | *concept> DinA5Letter
     | *concept> DinA4Letter
     | *concept> DinA3Letter
     | *concept> DinA2Letter
  }
)

variant WritingCard (
  label "WritingCard"
  description "a card for writing a message"
  type explicit
  
  specified.by NamedColor as {
      *str> namedColorAzure
    | *str> namedColorPink
    | *str> namedColorIvory
    | *str> namedColorWhite
  }
  
 
  specified.by Weight as {
      *float> Weight1P1
    | *float> Weight1P3
    | *float> Weight1P5
    | *float> Weight1P7
  }
 
  classified.as {
      *concept> DinA6CardConveyable
    | *concept> DinA6CardUnconveyable
    | *concept.derived> DinA6Entity
    }
)

variant FoldMethod12 (
  label "Fold-Method-Half-in-Size"
  description "a method to fold a piece of paper half in size"
  type explicit
  singleton true
)

variant FoldMethod13 (
  label "Fold-Method-a-third-in-Size"
  description "a method to fold a piece of paper a third in size"
  type explicit
  singleton true
)


variant FoldMethod14 (
  label "Fold-Method-a-quarter-in-Size"
  description "a method to fold a piece of paper a quarter in size"
  type explicit
  singleton true
)


variant Stamp (
  label "Stamp"
  description "a stamp indicating that the transport is paid"
  type explicit
  
  specified.by StampValue as {
      *int> Euro045
    | *int> Euro060
    | *int> Euro090
    | *int> Euro145
    | *int> Euro240
  }
)

variant LetterPackage (
  label "Letter,package"
  description "Implicit variant set up as subset of its 'constituting ps" 
  type implicit
)

PSIKEDS-IS-FULFILLED-BY-STATEMENTS:

purpose.system informRemoteAddressee (
  isFulfilledBy { 
    *pv> CompressedLetter 
    | *pv> UncompressedLetter  
    | *pv> Postcard
  }
)

purpose.system compressMessageMedia (
  isFulfilledBy { 
      *pv> FoldMethod12
    | *pv> FoldMethod13
    | *pv> FoldMethod14
  }
)

purpose.system protectMessage (
  isFulfilledBy { 
    *pv> Envelope
  }
)

purpose.system fileMessages ( 
  isFulfilledBy { 
      *pv> WritingPaper 
    | *pv> WritingCard
  }
)

purpose.system enableMessageTransport (
   isFulfilledBy { 
     *pv> Stamp
   }
 )

purpose.system prepareMultipleLetterWritings ( 
  isFulfilledBy {
    *pv> LetterPackage
  }
)

PSIKEDS-IS-CONSTITUTED-BY-STATEMENTS:

purpose.variant CompressedLetter ( 
  isConstitutedBy {
      < 1 of *ps> compressMessageMedia >
    & < 1 of *ps>  protectMessage >
    & < 1 of *ps>  fileMessages >
    & < 1 of *ps>  enableMessageTransport >
  }
)

purpose.variant UncompressedLetter ( 
  isConstitutedBy { 
    < 1 of *ps> protectMessage >
  & < 1 of *ps>  fileMessages >
  & < 1 of *ps>  enableMessageTransport >
  }
)

purpose.variant Postcard ( 
  isConstitutedBy { 
     < 1 of *ps> fileMessages >
   & < 1 of *ps> enableMessageTransport >
  }
)

purpose.variant.impl LetterPackage (
  isConstitutedBySubsetOf {
      *ps> fileMessages
    / *ps> enableMessageTransport 
    / *ps> protectMessage
  }
)

PSIKEDS-LOGICAL-INFERENCE-ELEMENTS:

event.var writingCardAsPostcard (
  label "the-writing-card-as-a-postcard"
  description "using the writing card for filing the message of a postcard"
  context [
    *pv> Postcard 
    *ps> fileMessages 
    ]
  fact *pv> WritingCard
)

event.str preparedAddressAreaOnWritingCardAsPostcard (
  label "preprinted-address-area-on-writing-card-as-postcard"
  description "writing card with preprinted address area being used as postcard"
  context [ 
    *pv> Postcard 
    *ps> fileMessages 
    *pv> WritingCard 
  ]
  fact *str> withPreprintedAddrField
)

event.int stamp045AsPostcardEnabler (
  label "045-cent-stamp-as-postcard-transport-enabler"
  description "45 cent stamp for enabling the transport of a postcard"
  context [ 
    *pv> Postcard 
    *ps> enableMessageTransport
    *pv> Stamp
  ]
  fact *int> Euro045 
)

event.var uncompressedLetterWithCardAsFileMedia (
  label "the-writing-card-used-as-uncompressed-letter-media"
  description "using the writing card for filing the message of an uncompressed letter"
  context [ 
    *pv> UncompressedLetter 
    *ps> fileMessages 
  ]
  fact *pv> WritingCard
)

event.str uncompressedLetterWritingCardWithoutAddressField (
  label "an-uncompressed-letter-with-a-writing-card-without-addr-field"
    description "no preprinted address area on a writing card used as uncompressed letter"
    context [ 
      *pv> UncompressedLetter 
      *ps> fileMessages 
      *pv> WritingCard 
    ]
    NOT
    fact *str> withPreprintedAddrField
)

event.concept uncompressedLetterInDinC6Envelope (
  label "uncompressed-letter-in-a-DIN-C6-envelope"
  context [ 
    *pv> UncompressedLetter 
    *ps> protectMessage
    *pv> Envelope
  ]
  fact *concept> DinC6Envelope
)


event.float UncompressedDinC6Letter (
  label "uncompressed-Din-C6-Letter"
  description "a typical DIN C6 Letter denoted by its Din-Number"
  context [ 
    *pv> UncompressedLetter
  ]
  fact *float> DinC6Number
)

/* Note: Letter size 5.6 for uncompressed letter not defined */
event.concept UncompressedDinC5Letter (
  label "uncompressed-Din-C5-Letter"
  description "a typical DIN C5 Letter denoted by the concept of all DIN-C6-features of its envelope"
  context [ 
    *pv> UncompressedLetter 
    *ps> protectMessage 
    *pv> Envelope
  ]
  fact *concept> DinC5Envelope
)

event.float UncompressedDinC4Letter (
  label "uncompressed-Din-C4-Letter"
  description "a typical DIN C4 Letter denoted by the Din-Number of the envelope"
  context [ 
    *pv> UncompressedLetter 
    *ps> protectMessage 
    *pv> Envelope
  ]
  fact *float> DinC4Number
)

event.int UncompressedLetterStamp060Cent (
  label "uncompressed-letter-stamp-with-value-60"
  context [ 
    *pv> UncompressedLetter 
    *ps> enableMessageTransport 
    *pv> Stamp
  ]
  fact *int> Euro060
)

event.int UncompressedLetterStamp145Cent (
  label "uncompressed-letter-stamp-with-value-60"
  context [ 
    *pv> UncompressedLetter 
    *ps> enableMessageTransport 
    *pv> Stamp
  ]
  fact *int> Euro145
)

event.var compressedLetterWithPaper (
  label "the-compressed-letter-written-on-paper"
  context [ 
    *pv> CompressedLetter 
    *ps> fileMessages
  ]
  fact *pv> WritingPaper
)

event.int compressedLetterWithDinA2Paper (
  label "compressed-letter-with-DIN-A2-paper"
  description "DIN-A2 paper in the context of a compressed letter"
  context [ 
    *pv> CompressedLetter 
    *ps> fileMessages 
    *pv> WritingPaper
  ]
  fact *int> DinA2Number
)

event.int compressedLetterWithDinA3Paper (
  label "compressed-letter-with-DIN-A3-paper"
  description "DIN-A3 paper in the context of a compressed letter"
  context [ 
    *pv> CompressedLetter 
    *ps> fileMessages 
    *pv> WritingPaper
  ]
  fact *int> DinA3Number
)

event.int compressedLetterWithDinA4Paper (
  label "compressed-letter-with-DIN-A4-paper"
  description "DIN-A4 paper in the context of a compressed letter"
  context [ 
    *pv> CompressedLetter 
    *ps> fileMessages 
    *pv> WritingPaper
    ]
  fact *int> DinA4Number
)

event.int compressedLetterWithDinA5Paper (
  label "compressed-letter-with-DIN-A5-paper"
  description "DIN-A5 paper in the context of a compressed letter"
  context [ 
    *pv> CompressedLetter 
    *ps> fileMessages 
    *pv> WritingPaper 
  ]
  fact *int> DinA5Number
)

event.float compressedLetterWithDinC4Envelope (
  label "compressed-letter-with-DIN-C4-envelope"
  description "DIN-C4 envelope in the context of a compressed letter"
  context [ 
    *pv> CompressedLetter 
    *ps> protectMessage 
    *pv> Envelope  
  ]
  fact *float> DinC4Number
)

event.float compressedLetterWithDinC5Envelope (
  label "compressed-letter-with-DIN-C5-envelope"
  description "DIN-C5 envelope in the context of a compressed letter"
  context [ 
    *pv> CompressedLetter 
    *ps> protectMessage 
    *pv> Envelope 
  ]
  fact *float> DinC5Number
)

event.float compressedLetterWithDinC56Envelope (
  label "compressed-letter-with-DIN-C56-envelope"
  description "DIN-C56 envelope in the context of a compressed letter"
  context [ 
    *pv> CompressedLetter 
    *ps> protectMessage 
    *pv> Envelope 
  ]
  fact *float> DinC56Number
)

event.float compressedLetterWithoutDinC56Envelope (
  label "compressed-letter-without-DIN-C56-envelope"
  description "not using a DIN-C56 envelope in the context of a compressed letter"
  context [ 
    *pv> CompressedLetter 
    *ps> protectMessage 
    *pv> Envelope 
  ]
  NOT
  fact *float> DinC56Number
)

event.float compressedLetterWithDinC6Envelope (
  label "compressed-letter-with-DIN-C6-envelope"
  description "DIN-C6 envelope in the context of a compressed letter"
  context [ 
    *pv> CompressedLetter 
    *ps> protectMessage 
    *pv> Envelope 
  ]
  fact *float> DinC6Number
)

event.float compressedLetterWithoutDinC6Envelope (
  label "compressed-letter-without-DIN-C6-envelope"
  description "not using a DIN-C6 envelope in the context of a compressed letter"
  context [ 
    *pv> CompressedLetter 
    *ps> protectMessage 
    *pv> Envelope 
  ]
  NOT
  fact *float> DinC6Number
)
 event.var compressedLetterFoldHalfInSize (
  label "compressed-letter-using-the-half-in-size-folding-method"
  context [ 
    *pv> CompressedLetter 
    *ps> compressMessageMedia 
  ]
  fact *pv> FoldMethod12
)

 event.var compressedLetterFoldThirdInSize (
  label "compressed-letter-using-the-third-in-size-folding-method"
  context [ 
    *pv> CompressedLetter 
    *ps> compressMessageMedia 
  ]
  fact *pv> FoldMethod13
)

 event.var compressedLetterFoldQuarterInSize (
  label "compressed-letter-using-the-quarter-in-size-folding-method"
  context [ 
    *pv> CompressedLetter 
    *ps> compressMessageMedia 
  ]
  fact *pv> FoldMethod14
)


event.float compressedDinC6Letter (
  label "compressed-Din-C6-Letter"
  description "a typical compressed DIN C6 Letter denoted by its Din-Number"
  context [ 
    *pv> CompressedLetter 
  ]
  fact *float> DinC6Number
)

event.float compressedDinC56Letter (
  label "compressed-Din-C56-Letter"
  description "a typical compressed DIN C56 Letter denoted by its Din-Number"
  context [ 
    *pv> CompressedLetter 
  ]
  fact *float> DinC56Number
)

event.float compressedDinC5Letter (
  label "compressed-Din-C5-Letter"
  description "a typical compressed DIN C5 Letter denoted by its Din-Number"
  context [ 
    *pv> CompressedLetter 
  ]
  fact *float> DinC5Number
)

event.float compressedDinC4Letter (
  label "compressed-Din-C4-Letter"
  description "a typical compressed DIN C4 Letter denoted by its Din-Number"
  context [ 
    *pv> CompressedLetter 
  ]
  fact *float> DinC4Number
)

event.int compressedLetterStamp060Cent (
  label "uncompressed-letter-stamp-with-value-60"
  context [ 
    *pv> CompressedLetter 
    *ps> enableMessageTransport 
    *pv> Stamp 
  ]
  fact *int> Euro060
)

event.int compressedLetterStamp145Cent (
  label "uncompressed-letter-stamp-with-value-145"
  context [ 
    *pv> CompressedLetter 
    *ps> enableMessageTransport 
    *pv> Stamp 
  ]
  fact *int> Euro145
)


PSIKEDS-RELATIONAL-INFERENCE-ELEMENTS:

relation.param PostcardColor (
  label "Postcard-Color"
  context [ 
    *pv> Postcard 
    ]
  value.type *sensor> NamedColor
)

relation.param WritingCardPostcardColor (
  label "Writing-Card-Color-Used-As-Postcard"
  context [ 
    *pv> Postcard 
    *ps> fileMessages 
    *pv> WritingCard 
  ]
  value.type *sensor> NamedColor
)

relation.param uncompressedLetterEnvelopDinSize (
  label "uncompressed-letter-envelope-din-size"
  context [ 
    *pv> UncompressedLetter 
    *ps> protectMessage 
    *pv> Envelope 
  ]
  value.type *sensor> DinCNumber
)

relation.param uncompressedLetterEnvelopColor (
  label "uncompressed-letter-envelope-color"
  context [ 
    *pv> UncompressedLetter 
    *ps> protectMessage 
    *pv> Envelope 
  ]
  value.type *sensor> NamedColor
)


relation.param uncompressedLetterPaperDinSize (
  label "uncompressed-letter-paper-din-size"
  context [ 
    *pv> UncompressedLetter 
    *ps> fileMessages 
    *pv> WritingPaper 
  ]
  value.type *sensor> DinANumber
)


relation.param uncompressedLetterDinSize (
  label "uncompressed-letter-din-size"
  description "the size of an uncompressed letter itself"
  context [ 
    *pv> UncompressedLetter 
  ]  
  value.type *sensor> DinCNumber
)

relation.param uncompressedLetterColor (
  label "uncompressed-letter-color"
  description "the color of an uncompressed letter itself"
  context [ 
    *pv> UncompressedLetter 
  ]
  value.type *sensor> NamedColor
)

relation.param compressedLetterEnvelopeColor (
  label "compressed-letter-envelope-color"
  description "the color of the envelope of a compressed"
  context [ 
    *pv> CompressedLetter 
    *ps> protectMessage 
    *pv> Envelope
  ]
  value.type *sensor> NamedColor
)

relation.param compressedLetterDinSize (
  label "compressed-letter-din-size"
  description "the size of the compressed letter itself"
  context [ 
    *pv> CompressedLetter 
  ]
  value.type *sensor> DinCNumber
)

relation.param compressedLetterColor (
  label "compressed-letter-color"
  description "the color of the compressed letter itself"
  context [ 
    *pv> CompressedLetter 
  ]
  value.type *sensor> NamedColor
)


relation.param compressedLetterPaperSize (
  label "compressed-letter-paper-size"
  description "size of the paper in the context of a compressed letter"
  context [ 
    *pv> CompressedLetter 
    *ps> fileMessages 
    *pv> WritingPaper 
  ]
  value.type *sensor> DinANumber
)

relation.param compressedLetterEnvelopeSize (
  label "compressed-letter-envelope-size"
  description "size of the envelope in the context of a compressed letter"
  context [ 
    *pv> CompressedLetter 
    *ps> protectMessage 
    *pv> Envelope 
  ]
  value.type *sensor> DinCNumber
)

PSIKEDS-INFERENCE-CONDITIONS:

/* RULE-1.A */
logic.enforcer IFpostcardTHENuseWritingCard (
  label "postcard=>use-writing-card"
  description "Rule 1.A: a postcard must be written on a writing card"
  nexus *pv> Postcard
  evokes *ev> writingCardAsPostcard
)

/* RULE-1.B */
logic.enforcer IFpostcardTHENuseWritingCardWithAddressField (
  label "postcard=>use-writing-card-with-preprinted-address-field"
  description "Rule 1.B: a postcard must be written on 
    a writing card with preprinted address field"
  nexus *pv> Postcard  
  evokes *ev> preparedAddressAreaOnWritingCardAsPostcard
)

/* RULE-1.C */
logic.enforcer IFpostcardTHENuse45CentStamp (
  label "postcard=>45-cent-stamp"
  description "Rule 1.C : if-then-rule : a postcard costs 45 cents"
  nexus *pv> Postcard  
  evokes *ev> stamp045AsPostcardEnabler 
)

/* RULE-1.D */
relation.normal ColorOfPostCardIsColorOfWritingCard (
  label "color-of-the-postcard-is-the-color-of-the-writing-card"
  description "Rule 1.D : relation"
  nexus *pv> Postcard   
  means [ *val> PostcardColor 
       eq *val> WritingCardPostcardColor]
)

/* RULE-2.A */
logic.enforcer IFuncompLetterTHENuseCardWithoutAddressField (
  label "uncompressed-letter-on-a-card=>card-without-preprinted-address-field"
  description "Rule 2.A : if-then-rule"
  nexus *pv> UncompressedLetter 
  evokes *ev> uncompressedLetterWritingCardWithoutAddressField
)

/* RULE-2.B */
logic.rule IfuncompLetterWithCardTHENuseC6Envelope (
  label "uncompressed-letter-on-a-card=>uncompressedLetterInDinC6Envelope" 
  description "Rule-2.B : if-then-rule"
  nexus *pv> UncompressedLetter 
  induces [ 
    {  *ev> uncompressedLetterWithCardAsFileMedia }
    => *ev> uncompressedLetterInDinC6Envelope ]
 )

/* RULE-2.C */
relation.normal EnvelopeDinSizeOfUncomLettIsLessOrEqualPaperDinSize (
  label "the-size-of-an-uncompressed-letter-envelope-is-less-or-equal-its-paper-din-size"
  description "Rule 2.C : relation"
  nexus *pv> UncompressedLetter 
  means [ *val> uncompressedLetterEnvelopDinSize 
      leq *val> uncompressedLetterPaperDinSize]    
)

/* RULE-2.D */
relation.normal SizeOfUncomLettIsSizeOfEnevlope (
  label "the-size-of-an-uncompressed-letter-is-the-size-of-its-envelope"
  description "Rule 2.D : relation"
  nexus *pv> UncompressedLetter 
  means [ *val> uncompressedLetterDinSize 
       eq *val> uncompressedLetterEnvelopDinSize]    
)

/* RULE-2.E */
relation.normal ColorOfUncompLetterIsColorOfEnvelope (
  label "color-of-an-uncompressed-letter-is-color-of-its-envelope"
  description "Rule 2.G : relation"
  nexus *pv> UncompressedLetter 
  means [ *val> uncompressedLetterColor 
       eq *val> uncompressedLetterEnvelopColor]    
)

/* RULE-2.F.a */
logic.rule IFuncompDinC6LetterTHENuse60CentStamp (
  label "uncompressed-DINC6-letter=>60-cent-stamp"
  description "Rule 2.F.a : if-then-rule"
  nexus *pv> UncompressedLetter 
  induces [ 
    {  *ev> UncompressedDinC6Letter } 
    => *ev> UncompressedLetterStamp060Cent ]
)

/* RULE-2.F.b */
logic.rule IFuncompDinC5LetterTHENuse145CentStamp (
  label "uncompressed-DINC5-letter=>145-cent-stamp"
  description "Rule 2.F.b : if-then-rule"
  nexus *pv> UncompressedLetter 
  induces [ 
    {  *ev> UncompressedDinC5Letter } 
    => *ev> UncompressedLetterStamp145Cent ]
)

/* RULE-2.F.c */
logic.rule IFuncompDinC4LetterTHENuse145CentStamp (
  label "uncompressed-DINC4-letter=>145-cent-stamp"
  description "Rule 2.F.c : if-then-rule"
  nexus *pv> UncompressedLetter 
  induces [ 
    {  *ev> UncompressedDinC4Letter } 
    => *ev> UncompressedLetterStamp145Cent ]
 )

/* RULE-2.G */
relation.normal PaperDinSizeGreaterThenDinA3inUncompressedLetters (
  label "paper-size-greater-than-DIN-A-3-in-uncompressed-letters"
  description "Rule 2.G: relation"
  nexus *pv> UncompressedLetter 
  means [ const *int> DinA3Number 
    less *val> uncompressedLetterPaperDinSize]    
)

/* RULE-3.A */
logic.enforcer IFcompressedLetterTHENusePaper (
  label "compressed-letter=>use-paper"
  description "Rule 3.A : if-then-rule : foldable letters must be written on paper"
  nexus *pv> CompressedLetter 
  evokes *ev> compressedLetterWithPaper 
)

/* RULE 3.B */  
relation.normal EnvelopeDinSizeOfCompLettIsLessThanPaperDinSize (
  label "the-size-of-an-compressed-letter-envelope-is-less-than-its-paper-din-size"
  description "Rule 3.B : relation"
  nexus *pv> CompressedLetter 
  means [ *val> compressedLetterEnvelopeSize 
     less *val> compressedLetterPaperSize ]    
)

/* A2 paper only in C4 envelope by using the method 'quarter in size' */
/* RULE-3.C.a */
logic.rule IFcompLetterWithDinA2PaperTHENfoldQuarterInSize (
  label "Convey-Din-A2-Paper-only-folded-a-quarter-in-size"
  description "Rule 3.C.A : if-then-rule" 
  nexus *pv> CompressedLetter 
  induces [
    {  *ev> compressedLetterWithDinA2Paper } 
    => *ev> compressedLetterFoldQuarterInSize ]
 )

/* RULE-3.C.b */
logic.rule IFcompLetterWithDinA2PaperThenOnlyDINC4Envelope (
  label "Convey-Din-A2-Paper-only-in-Din-C4-Envelope"
  description "Rule 3.C.C : if-then-rule"
  nexus *pv> CompressedLetter 
  induces [
    {  *ev> compressedLetterWithDinA2Paper } 
    => *ev> compressedLetterWithDinC4Envelope ]
 )

/* RULE-3.D.a */
logic.rule IFA3PaperInC4EnvelopenTHENhalfInSize (
  label "A3-Paper-in-C4-envelope-must-be-folded-half-in-size"
  description "Rule-3.D.a : if-then-rule"
  nexus *pv> CompressedLetter 
  induces [
    {  *ev> compressedLetterWithDinA3Paper 
    &  *ev> compressedLetterWithDinC4Envelope } 
    => *ev> compressedLetterFoldHalfInSize ]
 )
      
/* RULE-3.D.b */ 
logic.rule IFA3PaperInC5EnvelopenTHENquarterInSize (
  label "A3-paper-in-C5-envelope-must-be-folded-quarter-in-size"
  description "Rule-3.D.b : if-then-rule"
  nexus *pv> CompressedLetter 
  induces [
    {  *ev> compressedLetterWithDinA3Paper 
    &  *ev> compressedLetterWithDinC5Envelope } 
    => *ev> compressedLetterFoldQuarterInSize ]
)
       
/* A3 paper only in C4 or C5 envelope, hence not in C6 or C56 ... */
/* RULE-3.D.c */
logic.rule IFcompLetterWithDinA3PaperThenNotDinC56Envelope (
  label "Dont-Convey-Din-A3-Paper-in-Din-C56-Envelope"
  description "Rule 3.D.c : if-then-rule"
  nexus *pv> CompressedLetter 
  induces [
    {  *ev>  compressedLetterWithDinA3Paper } 
    => *ev> compressedLetterWithoutDinC56Envelope ]
)

/* RULE-3.D.d */
logic.rule IFcompLetterWithDinA3PaperThenNotDinC6Envelope (
  label "Dont-Convey-Din-A3-Paper-in-Din-C6-Envelope"
  description "Rule 3.D.d : if-then-rule"
  nexus *pv> CompressedLetter 
  induces [
    {  *ev>  compressedLetterWithDinA3Paper } 
    => *ev> compressedLetterWithoutDinC6Envelope ]
)
 
/* A4 paper only in C5, C56, C6 envelope by ... */
/* RULE-3.E.a */ 

logic.rule IFA4PaperInC5EnvelopenTHENhalfInSize (
  label "A4-Paper-in-C5-envelope-must-be-folded-half-in-size"
  description "Rule-3.E.a : if-then-rule"
  nexus *pv> CompressedLetter 
  induces [
    {  *ev> compressedLetterWithDinA4Paper 
    &  *ev> compressedLetterWithDinC5Envelope } 
    => *ev> compressedLetterFoldHalfInSize ]
)
       
/* RULE-3.E.b */ 
logic.rule IFA4PaperInC56EnvelopenTHENthirdInSize (
  label "A4-Paper-in-C56-envelope-must-be-folded-third-in-size"
  description "Rule-3.E.b : if-then-rule"
  nexus *pv> CompressedLetter 
  induces [
    {  *ev> compressedLetterWithDinA4Paper 
    &  *ev> compressedLetterWithDinC56Envelope } 
    => *ev> compressedLetterFoldThirdInSize ]
)
         
/* RULE-3.E.c */
logic.rule IFA4PaperInC6EnvelopenTHENquarterInSize (
  label "A4-Paper-in-C6-envelope-must-be-folded-quarter-in-size"
  description "Rule-3.E.c : if-then-rule"
  nexus *pv> CompressedLetter 
  induces [
    {  *ev>  compressedLetterWithDinA4Paper 
    &  *ev>  compressedLetterWithDinC6Envelope } 
    => *ev> compressedLetterFoldQuarterInSize ]
)
       
/* RULE-3.F.a */  
logic.rule IFfoldingThirdInSizeTHENdinC56Envelope (
  label "folding-third-in-size-requires-a-C56-envelope"
  description "Rule 3.F.a : if-then-rule" 
  nexus *pv> CompressedLetter 
  induces [
    {  *ev> compressedLetterFoldThirdInSize} 
    => *ev> compressedLetterWithDinC56Envelope ]
 )

/* RULE-3.F.b */  
logic.rule IFfoldingThirdInSizeTHENa4Paper (
  label "folding-third-in-size-requires-a-A4-paper"
  description "Rule 3.F.b : if-then-rule" 
  nexus *pv> CompressedLetter 
  induces [
    {  *ev> compressedLetterFoldThirdInSize } 
    => *ev> compressedLetterWithDinA4Paper ]
)

/* A5 paper only in C6 envelope by ... */ 
/* RULE-3.G */ 
logic.rule IfcompLetterWithA5PaperTHENfoldHalfInSize (
  label "A5-Paper-in-C6-envelope-must-be-folded-half-in-size"
  description "Rule-3.G : if-then-rule"
  nexus *pv> CompressedLetter 
  induces [
    {  *ev> compressedLetterWithDinA5Paper } 
    => *ev> compressedLetterFoldHalfInSize ]
 )

/* RULE-3.H.a */
relation.normal SizeOfComLettIsSizeOfEnevlope (
  label "size-of-a-compressed-letter-is-the-size-of-its-envelope" 
  description "Rule 3.H.a : relation"
  nexus *pv> CompressedLetter 
  means [ *val> compressedLetterEnvelopeSize 
       eq *val> compressedLetterDinSize]    
)

/* RULE-3.H.b */
relation.normal ColorOfCompLetterIsColorOfEnvelope (
  label "color-of-a-compressed-letter-is-the-color-of-its-envelope"
  description "Rule 3.H.b : relation"
  nexus *pv> CompressedLetter 
  means [ *val> compressedLetterEnvelopeColor 
       eq *val> compressedLetterColor]    
)

/* RULE-3.I.a */
logic.rule IFcompC6LetterTHEN60CentStamp (
  label "compressed-C6-letter=>60-cent-stamp"
  description "Rule 3.I.a : if-then-rule"
  nexus *pv> CompressedLetter 
  induces [ { 
      *ev> compressedDinC6Letter
    } => *ev>  compressedLetterStamp060Cent ]
 )

/* RULE-3.I.b */ 
logic.rule IFcompC56LetterTHEN60CentStamp (
  label "compressed-C56-letter=>60-cent-stamp"
  description "Rule 3.I.b : if-then-rule"
  nexus *pv> CompressedLetter 
  induces [
  {  *ev> compressedDinC56Letter } 
  => *ev> compressedLetterStamp060Cent ]
 )

/* RULE-3.I.c */ 
logic.rule IFcompC5LetterTHEN145CentStamp (
  label "compressed-C5-letter=>145-cent-stamp"
  description "Rule 3.I.c : if-then-rule" 
  nexus *pv> CompressedLetter 
  induces [
    {  *ev> compressedDinC5Letter } 
    => *ev> compressedLetterStamp145Cent ]
)

/* RULE-3.I.d */ 
logic.rule IFcompC4LetterTHEN145CentStamp (
  label "compressed-C4-letter=>145-cent-stamp"
  description "Rule 3.I.d : if-then-rule" 
  nexus *pv> CompressedLetter 
  induces [ 
    {  *ev> compressedDinC4Letter } 
    => *ev> compressedLetterStamp145Cent ]
 )

/* RULE-3.K.a */
relation.qualified compressThirdASizeOnlyIfDinCeuqal56 (
  label "folding-third-in-size-requires-envelope-with-dinC-Number=5,6"
  description "Rule 3.K.a : conditioned-relation"
  nexus *pv> CompressedLetter 
  means < *ev> compressedLetterFoldThirdInSize 
          => [ *val> compressedLetterEnvelopeSize 
             eq const *float> DinC56Number ] >
 )
  
relation.qualified compressThirdASizeOnlyIfPaperDinANumberlessConstA5 (
  label "folding-third-in-size-requires-paper-with-dinA-number-less-then-5"
  description "Rule 3.K.b : conditioned-relation"
  nexus *pv> CompressedLetter 
  means < *ev> compressedLetterFoldThirdInSize 
           => [ *val> compressedLetterPaperSize 
             less const *int> DinA5Number ] >
 )

relation.qualified compressThirdASizeOnlyIfConstA3lessPaperDinANumber (
  label "folding-third-in-size-requires-paper-with-dinA-number-greater-then-3"
  description "Rule 3.K.c : conditioned-relation"
  nexus *pv> CompressedLetter 
  means < *ev> compressedLetterFoldThirdInSize 
           => [ const *int> DinA3Number 
            less *val> compressedLetterPaperSize] >
 )


