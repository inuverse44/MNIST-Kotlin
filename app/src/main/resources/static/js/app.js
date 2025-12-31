document.addEventListener('DOMContentLoaded', () => {
    const canvas = document.getElementById('canvas');
    const ctx = canvas.getContext('2d');
    const clearBtn = document.getElementById('clearBtn');
    const predictBtn = document.getElementById('predictBtn');
    const predictionEl = document.getElementById('prediction');
    const confidenceEl = document.getElementById('confidence');
    const barsContainer = document.getElementById('bars');

    let isDrawing = false;

    // Init probability bars
    for (let i = 0; i < 10; i++) {
        const wrapper = document.createElement('div');
        wrapper.className = 'bar-wrapper';
        wrapper.innerHTML = `
            <div class="bar-value" id="val-${i}">0%</div>
            <div class="bar" style="height: 2%" id="bar-${i}"></div>
            <div class="bar-label">${i}</div>
        `;
        barsContainer.appendChild(wrapper);
    }

    // Canvas settings
    ctx.fillStyle = 'black';
    ctx.fillRect(0, 0, canvas.width, canvas.height);
    ctx.lineWidth = 18;
    ctx.lineCap = 'round';
    ctx.lineJoin = 'round';
    ctx.strokeStyle = 'white';

    function getPos(e) {
        const rect = canvas.getBoundingClientRect();
        const clientX = e.touches ? e.touches[0].clientX : e.clientX;
        const clientY = e.touches ? e.touches[0].clientY : e.clientY;
        
        // Scale factor between CSS and Canvas pixels
        const scaleX = canvas.width / rect.width;
        const scaleY = canvas.height / rect.height;
        
        return {
            x: (clientX - rect.left) * scaleX,
            y: (clientY - rect.top) * scaleY
        };
    }

    function startDraw(e) {
        e.preventDefault();
        isDrawing = true;
        const pos = getPos(e);
        ctx.beginPath();
        ctx.moveTo(pos.x, pos.y);
    }

    function draw(e) {
        e.preventDefault();
        if (!isDrawing) return;
        const pos = getPos(e);
        ctx.lineTo(pos.x, pos.y);
        ctx.stroke();
    }

    function stopDraw() {
        isDrawing = false;
    }

    // Desktop
    canvas.addEventListener('mousedown', startDraw);
    canvas.addEventListener('mousemove', draw);
    canvas.addEventListener('mouseup', stopDraw);
    canvas.addEventListener('mouseout', stopDraw);
    
    // Mobile
    canvas.addEventListener('touchstart', startDraw);
    canvas.addEventListener('touchmove', draw);
    canvas.addEventListener('touchend', stopDraw);

    clearBtn.addEventListener('click', () => {
        ctx.fillStyle = 'black';
        ctx.fillRect(0, 0, canvas.width, canvas.height);
        predictionEl.textContent = '-';
        confidenceEl.textContent = '';
        
        // Reset bars
        document.querySelectorAll('.bar').forEach(b => {
            b.style.height = '2%';
            b.classList.remove('active');
        });
        document.querySelectorAll('.bar-value').forEach(v => {
            v.style.opacity = '0';
        });
    });

    predictBtn.addEventListener('click', async () => {
        const imageData = getMnistData();
        
        try {
            const response = await fetch('/api/predict', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ image: imageData })
            });
            
            if (!response.ok) throw new Error('Network response was not ok');
            
            const result = await response.json();
            displayResult(result);
        } catch (error) {
            console.error('Error:', error);
            predictionEl.textContent = 'Error';
        }
    });

    function getMnistData() {
        const tempCanvas = document.createElement('canvas');
        tempCanvas.width = 28;
        tempCanvas.height = 28;
        const tempCtx = tempCanvas.getContext('2d');
        
        // Center of mass or bounding box normalization could be added here for better accuracy
        tempCtx.drawImage(canvas, 0, 0, 28, 28);
        
        const imgData = tempCtx.getImageData(0, 0, 28, 28);
        const data = imgData.data;
        const input = [];
        
        for (let i = 0; i < data.length; i += 4) {
            // Take the R channel (it's grayscale white-on-black)
            input.push(data[i] / 255.0);
        }
        
        return input;
    }

    function displayResult(result) {
        predictionEl.textContent = result.digit;
        
        result.probabilities.forEach((prob, i) => {
            const bar = document.getElementById(`bar-${i}`);
            const val = document.getElementById(`val-${i}`);
            
            // Format percentage (e.g., "98%")
            // If < 1%, show empty or "<1"
            const percentage = (prob * 100).toFixed(0);
            val.textContent = percentage + '%';
            
            // Show value only if meaningful probability
            val.style.opacity = prob > 0.01 ? '1' : '0';

            const height = Math.max(2, prob * 100); // Min height 2%
            bar.style.height = `${height}%`;
            
            if (i === result.digit) {
                bar.classList.add('active');
                val.style.fontWeight = 'bold';
                val.style.color = '#2196F3';
            } else {
                bar.classList.remove('active');
                val.style.fontWeight = 'normal';
                val.style.color = '#555';
            }
        });
        
        const maxProb = result.probabilities[result.digit];
        confidenceEl.textContent = `Confidence: ${(maxProb * 100).toFixed(1)}%`;
    }
});
