 <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
 <header id="top">
 <form class="sp">
                <div class="sbTop">
                    <i></i><span><span>Saved Searches</span></span>
                    <ul class="dropdown" id="savedsearches">
                    </ul>
                </div>
                
                <button class="jsNewSearch">New Search</button>
            </form>
            <form class="sp">
                <input type="text" placeholder="Name Searches" id="searchName"/>
                <button type="button" class="save-search" onClick="saveSearch()" disabled="disabled">Save Search</button>
            </form>
            <form class="sp">
                <input type="text" id="qksearchtxt" placeholder="Quick Searches" class="is"/>
                <button type="button" onclick="quickSearch()" id="qksearchbtn">Search</button>
            </form>
            <a class="btn logout il" href="j_spring_security_logout">Logout</a>
</header>

<script>
function removeSearch(obj) {
	var r = false;
	jQuery.ajax({
		type: 'POST',
		url:    'ausc/removesearch',
		data: {adminUserSearchId : $(obj).attr('data-i')},
        success: function(result) {
                	r = true;
        		 },
        async:   false
   });
	return r;
}
</script>