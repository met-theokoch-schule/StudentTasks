const ObjectViewer = (function() {
    let objects = new Map();
    let container = null;
    let viewer = null;

    function init() {
        container = document.getElementById('objectViewerContainer');
        viewer = document.getElementById('objectViewer');
        
        if (!container || !viewer) {
            console.error('Object Viewer elements not found');
            return;
        }
        
        console.log('Object Viewer initialized');
    }

    function show() {
        if (container) {
            container.style.display = 'block';
        }
    }

    function hide() {
        if (container) {
            container.style.display = 'none';
        }
    }

    function clear() {
        objects.clear();
        if (viewer) {
            viewer.innerHTML = '';
        }
        hide();
    }

    function createCard(objectData) {
        const card = document.createElement('div');
        card.className = 'object-card';
        card.id = `obj-card-${objectData.id}`;

        if (objectData.imageUrl) {
            const img = document.createElement('img');
            img.className = 'object-card-image';
            img.src = objectData.imageUrl;
            img.alt = objectData.className;
            img.onerror = function() {
                this.style.display = 'none';
            };
            card.appendChild(img);
        }

        const attributesContainer = document.createElement('div');
        attributesContainer.className = 'object-card-attributes';

        objectData.attributes.forEach(attr => {
            const attrDiv = document.createElement('div');
            attrDiv.className = 'object-card-attribute';

            const label = document.createElement('span');
            label.className = 'object-card-label';
            label.textContent = (attr.label || attr.name) + ':';

            const value = document.createElement('span');
            value.className = 'object-card-value';
            value.textContent = String(attr.value);

            attrDiv.appendChild(label);
            attrDiv.appendChild(value);
            attributesContainer.appendChild(attrDiv);
        });

        card.appendChild(attributesContainer);
        return card;
    }

    function createObject(objectData) {
        objects.set(objectData.id, objectData);
        
        const card = createCard(objectData);
        if (viewer) {
            viewer.appendChild(card);
        }

        if (objects.size === 1) {
            show();
        }
    }

    function updateObject(objectData) {
        if (!objects.has(objectData.id)) {
            return;
        }

        const existingObject = objects.get(objectData.id);
        
        objectData.attributes.forEach(updatedAttr => {
            const existingAttr = existingObject.attributes.find(a => a.name === updatedAttr.name);
            if (existingAttr) {
                existingAttr.value = updatedAttr.value;
            }
        });

        const card = document.getElementById(`obj-card-${objectData.id}`);
        if (card) {
            const newCard = createCard(existingObject);
            card.replaceWith(newCard);
        }
    }

    function deleteObject(objectId) {
        objects.delete(objectId);
        
        const card = document.getElementById(`obj-card-${objectId}`);
        if (card) {
            card.remove();
        }

        if (objects.size === 0) {
            hide();
        }
    }

    return {
        init,
        show,
        hide,
        clear,
        createObject,
        updateObject,
        deleteObject
    };
})();
