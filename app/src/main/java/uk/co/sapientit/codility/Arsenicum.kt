package uk.co.sapientit.codility

class Arsenicum {
    /*
    There are probably a lot of ways of creating palindromes, but how do you stop it from getting out of control?
    It would be easy to get into a loop and repate the same words again and again, and the trick to this problem
    is knowing when to abandon a loop.

    Using the example "by metal owl egg mr crow worm my ate"

    To build a plaindrom we start with a word at the front.  We will try mr.

    mr .......

    Now we need to add a word that ends with the reverse of the last 2 characters of mr - rm

    mr .... worm

    We are looking for a word that starts with the reverse of the first 2 characters of worm - ow

    mr owl ... worm

    and that is a palindrome.

    The key to spot here is that the only information I need to look for the next word is where I am in the current
    word and which direction I am going in.

    position 0 in mr - forward
    position 2 in worm - backward
    etc.

    Since S is at most 500 characters long, there are therefore a maximum of 1000 possible combinations to go through.
    If I find myself in a position and direction I have tried before then I just fail back and try the next word.

    In the code below I never extract the words, I always refer to them by the starting or ending position in the string.
    To make life simpler I add a prefix and suffix of ' ' so that each word is surrounded by spaces and I never
    need to worry about start and end of string.

    To track what is possible I make use of two Trie's = one from the start of each word and one from the end.  At each
    step on the Trie I record whether this is the end of a word (and if so which word - it doesn't matter if there
    are duplicate words as long as I record one of them).

    Also for each word I need to find out and record the positions of any palindromes at the start and end of the word.
    breeze has a plaindrom at the end so if we match the br then we can stop.  Likewise every has the sam palindrome
    at the start so I can stop if I match yr at the end of the word.  Mote that every word ends and starts
    in a palindrom of length 1.  This may not be actually necessary.

    If we have an anagram such as "madam im adam", if we don't do the precessing above we would still end up with
    a plaindrome of "madam im adam madam im adam" (repeating a palindrome gives another palindrome). But would this
    cause us to go over the 600,000 character limit?  Not sure.  Can't work out how to calculate worst case.


     */
    class Trie {
        var words = mutableListOf<Int>()
        var next = HashMap<Char, Trie>()
        var end = -1
    }

    val forTried = HashSet<Int>()  // positions we have already tried going forward
    val backTried = HashSet<Int>() // and backward
    val forward = Trie()
    val backward = Trie()
    val forPals = HashSet<Int>()  // palindromes at start of word
    val backPals = HashSet<Int>() // and at end
    val forList = mutableListOf<Int>() // list of words at the beginning
    val backList = mutableListOf<Int>() // and reverse list at the end
    var words = ""
    fun solution(S: String): String {
        val NO_ANSWER = "NO"
        words = " " + S + " "
        var index = 1
        val allWords = mutableListOf<Int>()
        while (index < words.length) {
            val wordStart = index
            backPals.add(wordStart)
            allWords.add(index)
            var current = forward
            while (words[index] != ' ') {
                current = current.next.getOrPut(words[index++]){ Trie() }
                current.words.add(wordStart)
            }
            current.end = wordStart
            val last = index - 1
            var first = index - 1
            while (first >= wordStart) {
                if (words[first] == words[last]) {
                    var low = first + 1
                    var high = last -1
                    while (low < high && words[low] == words[high]) {
                        low++
                        high--
                    }
                    if (low >= high) {
                        forPals.add(first)
                    }
                }
                first--
            }
            if (forPals.contains(wordStart)) {
                return words.substring(wordStart,index) // a single word is a palindrome in itself.
            }
            index++
        }
        index -= 2
        while (index > 0) {
            val wordStart = index
            var current = backward
            while (words[index] != ' ') {
                current = current.next.getOrPut(words[index--]){ Trie() }
                current.words.add(wordStart)
            }
            current.end = wordStart// Last character always a palindrome
            val last = index + 1
            var first = last
            while (first < wordStart) {
                if (words[first] == words[last]) {
                    var low = first - 1
                    var high = last + 1
                    while (low > high && words[low] == words[high]) {
                        low--
                        high++
                    }
                    if (low <= high) {
                        backPals.add(first)
                    }
                }
                first++
            }
            index--
        }
        for (word in allWords) { // go through all the words in the string and try them as a starting word.
            forList.add(word)
            if (tryForward(word)) { // Recursive function - we have characters going forwrd that need to be matched
                val builder = StringBuilder() // and build the answer
                for (f in forList) {
                    buildForward(builder,f)
                    builder.append(' ')
                }
                var first = true
                for (f in backList) {
                    if  (first) {
                        first = false
                    } else {
                        builder.append(' ')
                    }
                    buildBackWard(builder,f)
                }
                return String(builder)
            }
            forList.removeAt(forList.lastIndex) // remove the current word and move on to the next
        }
        return NO_ANSWER


    }
    fun buildForward(builder: StringBuilder, position: Int) {
        var index = position
        while (words[index] != ' ') {
            builder.append(words[index++])
        }
    }
    fun buildBackWard(builder: StringBuilder, position: Int) {
        var index = position
        while (words[index] != ' ') {
            index--
        }
        buildForward(builder,index + 1)
    }
    fun tryForward(position: Int) : Boolean{
        if (words[position] == ' ') return true // we were at the end of a word so palindrome found
        if (forPals.contains(position)) return true // the rest of the word is a palindrom - solution found
        if (forTried.contains(position)) return false // we have been here before - fail
        forTried.add(position) // record that we have been here before
        var index = position
        var current: Trie? = backward
        while (words[index] != ' ') {  // match Trie character by character
            current = current!!.next[words[index]]
            if (current == null) return false
            if (current.end >= 0) {  // We have found an end of the word in the Trie, so try using that word and
                backList.add(0,current.end) // continue in the same direction
                if (tryForward(index + 1)) {
                    return true
                }
                backList.removeAt(0) // didn't work.  Carry oin wiuth the Trie
            }
            index++
        }
        for (word in current!!.words) {  // These words all match the suffix we are matching
            backList.add(0, word) // so try one by one
            if (tryBackward(word - index + position)) return true
            backList.removeAt(0)
        }
        return false
    }
    fun tryBackward(position: Int) : Boolean{
        if (words[position] == ' ') return true
        if (backPals.contains(position)) return true
        if (backTried.contains(position)) return false
        backTried.add(position)
        var index = position
        var current: Trie? = forward
        while (words[index] != ' ') {
            current = current!!.next[words[index]]
            if (current == null) return false
            if (current.end >= 0) {
                forList.add(current.end)
                if (tryBackward(index - 1)) {
                    return true
                }
                forList.removeAt(forList.lastIndex)
            }
            index--
        }
        for (word in current!!.words) {
            forList.add( word)
            if (tryForward(word - index + position)) return true
            forList.removeAt(forList.lastIndex)
        }
        return false
    }
}